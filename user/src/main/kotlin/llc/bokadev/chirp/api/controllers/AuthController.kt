package llc.bokadev.chirp.api.controllers

import jakarta.validation.Valid
import llc.bokadev.chirp.api.dto.AuthenticatedUserDto
import llc.bokadev.chirp.api.dto.ChangePasswordRequest
import llc.bokadev.chirp.api.dto.EmailRequest
import llc.bokadev.chirp.api.dto.LoginRequest
import llc.bokadev.chirp.api.dto.RefreshRequest
import llc.bokadev.chirp.api.dto.RegisterRequest
import llc.bokadev.chirp.api.dto.ResetPasswordRequest
import llc.bokadev.chirp.api.dto.UserDto
import llc.bokadev.chirp.api.mappers.toAuthenticatedUserDto
import llc.bokadev.chirp.api.mappers.toUserDto
import llc.bokadev.chirp.service.auth.AuthService
import llc.bokadev.chirp.service.auth.EmailVerificationService
import llc.bokadev.chirp.service.auth.PasswordResetService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
    private val emailVerificationService: EmailVerificationService,
    private val passwordResetService: PasswordResetService
) {

    @PostMapping("/register")
    fun register(
        @Valid @RequestBody registerRequest: RegisterRequest
    ): UserDto {
        return authService.registerUser(
            email = registerRequest.email,
            username = registerRequest.username,
            password = registerRequest.password
        ).toUserDto()
    }

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody loginRequest: LoginRequest
    ): AuthenticatedUserDto {
        return authService.login(
            email = loginRequest.email,
            password = loginRequest.password
        ).toAuthenticatedUserDto()
    }

    @PostMapping("/refresh")
    fun refresh(
        @RequestBody refreshRequest: RefreshRequest
    ): AuthenticatedUserDto {
        return authService.refresh(refreshRequest.refreshToken).toAuthenticatedUserDto()
    }

    @GetMapping("/verify")
    fun verifyEmail(
        @RequestParam token: String
    ) {
        emailVerificationService.verifyEmail(token)
    }

    @PostMapping("/forgot-password")
    fun forgotPassword(
        @RequestBody emailRequest: EmailRequest
    ) {
        passwordResetService.requestPasswordReset(emailRequest.email)
    }

    @PostMapping("/reset-password")
    fun resetPassword(
        @RequestBody resetPasswordRequest: ResetPasswordRequest
    ) {
        passwordResetService.resetPassword(
            token = resetPasswordRequest.token,
            newPassword = resetPasswordRequest.newPassword
        )
    }

    @PostMapping("/change-password")
    fun changePassword(
        @RequestBody changePasswordRequest: ChangePasswordRequest
    ) {

        // TODO: Extract request user ID from jwt
//        passwordResetService.changePassword(
//            oldPassword = changePasswordRequest.oldPassword,
//            newPassword = changePasswordRequest.newPassword,
//            userId =
//        )


    }
}