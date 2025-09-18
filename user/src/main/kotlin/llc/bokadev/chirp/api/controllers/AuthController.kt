package llc.bokadev.chirp.api.controllers

import jakarta.validation.Valid
import llc.bokadev.chirp.api.dto.RegisterRequest
import llc.bokadev.chirp.api.dto.UserDto
import llc.bokadev.chirp.api.mappers.toUserDto
import llc.bokadev.chirp.service.auth.AuthService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/register")
    fun register(
        @Valid @RequestBody registerRequest: RegisterRequest
    ) : UserDto {
        return authService.registerUser(
            email = registerRequest.email,
            username = registerRequest.username,
            password = registerRequest.password
        ).toUserDto()
    }
}