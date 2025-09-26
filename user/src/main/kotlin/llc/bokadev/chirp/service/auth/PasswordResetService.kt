package llc.bokadev.chirp.service.auth

import jakarta.transaction.Transactional
import llc.bokadev.chirp.domain.events.user.UserEvent
import llc.bokadev.chirp.domain.exception.InvalidCredentialsException
import llc.bokadev.chirp.domain.exceptions.InvalidTokenException
import llc.bokadev.chirp.domain.exception.SamePasswordException
import llc.bokadev.chirp.domain.exception.UserNotFoundException
import llc.bokadev.chirp.domain.type.UserId
import llc.bokadev.chirp.infra.database.entities.PasswordResetTokenEntity
import llc.bokadev.chirp.infra.database.repositories.PasswordResetTokenRepository
import llc.bokadev.chirp.infra.database.repositories.RefreshTokenRepository
import llc.bokadev.chirp.infra.database.repositories.UserRepository
import llc.bokadev.chirp.infra.message_queue.EventPublisher
import llc.bokadev.chirp.infra.security.PasswordEncoder
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class PasswordResetService(
    private val userRepository: UserRepository,
    private val passwordResetTokenRepository: PasswordResetTokenRepository,
    @param:Value("\${chirp.email.reset-password.expiry-minutes}") private val expiryMinutes: Long,
    private val passwordEncoder: PasswordEncoder,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val eventPublisher: EventPublisher
) {

    @Transactional
    //In this case, for security we just silently return
    //as we want to send the email if and only if the acc exists
    //if we threw and exception it is a potential security issue
    fun requestPasswordReset(email: String) {
        val user = userRepository.findByEmail(email)
            ?: return

        passwordResetTokenRepository.invalidateActiveTokensForUser(user)

        val token = PasswordResetTokenEntity(
            user = user,
            expiresAt = Instant.now().plus(expiryMinutes, ChronoUnit.MINUTES)
        )

        passwordResetTokenRepository.save(token)

        eventPublisher.publish(
            event = UserEvent.RequestResetPassword(
                userId = user.id!!,
                email = user.email,
                username = user.username,
                verificationToken = token.token,
                expiresInMinutes = expiryMinutes
            )
        )
    }

    @Transactional
    fun resetPassword(token: String, newPassword: String) {
        val resetToken = passwordResetTokenRepository.findByToken(token)
            ?: throw InvalidTokenException("Invalid password reset token")

        if (resetToken.isUsed) {
            throw InvalidTokenException("Password reset token has already been used.")
        }

        if (resetToken.isExpired) {
            throw InvalidTokenException("Password reset token has expired.")
        }

        val user = resetToken.user

        if (passwordEncoder.matches(newPassword, user.hashedPassword)) {
            throw SamePasswordException()
        }
        val hashedNewPassword = passwordEncoder.encode(newPassword)
        userRepository.save(user.apply {
            if (hashedNewPassword != null) {
                this.hashedPassword = hashedNewPassword
            }
        })

        passwordResetTokenRepository.save(
            resetToken.apply {
                this.usedAt = Instant.now()
            }
        )

        refreshTokenRepository.deleteByUserId(user.id!!)

    }

    @Transactional
    fun changePassword(userId: UserId, oldPassword: String, newPassword: String) {
        val user = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()

        if (!passwordEncoder.matches(oldPassword, user.hashedPassword)) {
            throw InvalidCredentialsException()
        }

        if (oldPassword == newPassword) {
            throw SamePasswordException()
        }

        refreshTokenRepository.deleteByUserId(user.id!!)

        val hashedNewPassword = passwordEncoder.encode(newPassword)

        userRepository.save(user.apply {
            this.hashedPassword = hashedNewPassword!!
        })

    }
    @Scheduled(cron = "0 0 3 * * *")
    fun cleanupExpiredTokens() {
        passwordResetTokenRepository.deleteByExpiresAtLessThan(Instant.now())
    }
}