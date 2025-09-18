package llc.bokadev.chirp.service.auth

import llc.bokadev.chirp.domain.exception.UserAlreadyExistsException
import llc.bokadev.chirp.domain.model.User
import llc.bokadev.chirp.infra.database.entities.UserEntity
import llc.bokadev.chirp.infra.database.mappers.toUser
import llc.bokadev.chirp.infra.database.repositories.UserRepository
import llc.bokadev.chirp.infra.security.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
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
}