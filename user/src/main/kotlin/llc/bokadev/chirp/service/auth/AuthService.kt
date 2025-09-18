package llc.bokadev.chirp.service.auth

import llc.bokadev.chirp.domain.exception.InvalidCredentialsException
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
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.time.Instant
import java.util.Base64

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val refreshTokenRepository: RefreshTokenRepository
) {

    fun registerUser(email: String, username: String, password: String): User {
        val user = userRepository.findByEmailOrUsername(email.trim(), username.trim())

        if (user != null) {
            throw UserAlreadyExistsException()
        }

        val savedUser = userRepository.save(
            UserEntity(
                email = email.trim(),
                username = username.trim(),
                hashedPassword = passwordEncoder.encode(password)!!
            )
        ).toUser()

        return savedUser
    }


    fun login(email: String, password: String): AuthenticatedUser {
        val user = userRepository.findByEmail(email.trim())
            ?: throw InvalidCredentialsException()

        if (!passwordEncoder.matches(rawPassword = password, user.hashedPassword)) {
            throw InvalidCredentialsException()
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