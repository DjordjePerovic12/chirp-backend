package llc.bokadev.chirp.service.auth

import llc.bokadev.chirp.domain.exception.EmailNotVerifiedException
import llc.bokadev.chirp.domain.exception.InvalidCredentialsException
import llc.bokadev.chirp.domain.exception.InvalidTokenException
import llc.bokadev.chirp.domain.exception.UserAlreadyExistsException
import llc.bokadev.chirp.domain.exception.UserNotFoundException
import llc.bokadev.chirp.domain.model.AuthenticatedUser
import llc.bokadev.chirp.domain.model.User
import llc.bokadev.chirp.domain.model.UserId
import llc.bokadev.chirp.infra.database.entities.RefreshTokenEntity
import llc.bokadev.chirp.infra.database.entities.UserEntity
import llc.bokadev.chirp.infra.database.mappers.toUser
import llc.bokadev.chirp.infra.database.repositories.RefreshTokenRepository
import llc.bokadev.chirp.infra.database.repositories.UserRepository
import llc.bokadev.chirp.infra.security.PasswordEncoder
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.MessageDigest
import java.time.Instant
import java.util.*

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val emailVerificationService: EmailVerificationService
) {

    @Transactional
    fun registerUser(email: String, username: String, password: String): User {
        val user = userRepository.findByEmailOrUsername(email.trim(), username.trim())

        if (user != null) {
            throw UserAlreadyExistsException()
        }


        val savedUser = userRepository.save(
            UserEntity(
                email = email.trim(),
                username = username.trim(),
                hashedPassword = passwordEncoder.encode(password)!!,
            )
        ).toUser()

        emailVerificationService.createVerificationToken(email.trim())

        return savedUser
    }


    fun login(email: String, password: String): AuthenticatedUser {
        val user = userRepository.findByEmail(email.trim())
            ?: throw InvalidCredentialsException()

        if (!passwordEncoder.matches(rawPassword = password, user.hashedPassword)) {
            throw InvalidCredentialsException()
        }

        if(user.hasVerifiedEmail) {
            throw EmailNotVerifiedException()
        }

        return user.id?.let { userId ->
            val accessToken = jwtService.generateAccessToken(userId)
            val refreshToken = jwtService.generateRefreshToken(userId)

            storeRefreshToken(userId, refreshToken)

            AuthenticatedUser(
                user = user.toUser(),
                accessToken = accessToken,
                refreshToken = refreshToken,
            )
        } ?: throw UserNotFoundException()
    }

    @Transactional
    fun refresh(refreshToken: String): AuthenticatedUser {
        if (!jwtService.validateRefreshToken(refreshToken)) {
            throw InvalidTokenException(
                message = "Invalid refresh token"
            )
        }

        val userId = jwtService.getUserIdFromToken(refreshToken)
        val user = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()

        val hashed = hashToken(refreshToken)

        return user.id.let { userId ->
            refreshTokenRepository.findByUserIdAndHashedToken(
                userId!!, hashed
            ) ?: throw InvalidTokenException("Invalid token")

            refreshTokenRepository.deleteByUserIdAndHashedToken(
                userId = userId,
                hashedToken = hashed
            )
            val newAccessToken = jwtService.generateRefreshToken(userId)
            val newRefreshToken = jwtService.generateRefreshToken(userId)

            storeRefreshToken(userId, newRefreshToken)

            AuthenticatedUser(
                user = user.toUser(),
                accessToken = newAccessToken,
                refreshToken = newRefreshToken
            )
        }

    }

    private fun storeRefreshToken(userId: UserId, token: String) {
        val hashedToken = hashToken(token)
        val expiryMs = jwtService.refreshTokenValidityMs
        val expiresAt = Instant.now().plusSeconds(expiryMs)

        refreshTokenRepository.save(
            RefreshTokenEntity(
                userId = userId,
                expiresAt = expiresAt,
                hashedToken = hashedToken
            )
        )
    }

    private fun hashToken(token: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(token.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }
}