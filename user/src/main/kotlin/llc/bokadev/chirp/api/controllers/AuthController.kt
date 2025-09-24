package llc.bokadev.chirp.api.controllers

import jakarta.validation.Valid
import llc.bokadev.chirp.api.config.IpRateLimit
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
import llc.bokadev.llc.bokadev.chirp.api.util.requestUserId
import llc.bokadev.chirp.infra.rete_limiting.EmailRateLimiter
import llc.bokadev.chirp.service.auth.AuthService
import llc.bokadev.chirp.service.auth.EmailVerificationService
import llc.bokadev.chirp.service.auth.PasswordResetService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
    private val emailVerificationService: EmailVerificationService,
    private val passwordResetService: PasswordResetService,
    private val emailRateLimiter: EmailRateLimiter
) {


    @PostMapping("/register")
    @IpRateLimit(
        requests = 10,
        duration = 1L,
        unit = TimeUnit.HOURS
    )
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
    @IpRateLimit(
        requests = 10,
        duration = 1L,
        unit = TimeUnit.HOURS
    )
    fun login(
        @Valid @RequestBody loginRequest: LoginRequest
    ): AuthenticatedUserDto {
        return authService.login(
            email = loginRequest.email,
            password = loginRequest.password
        ).toAuthenticatedUserDto()
    }

    @PostMapping("/refresh")
    @IpRateLimit(
        requests = 10,
        duration = 1L,
        unit = TimeUnit.HOURS
    )
    fun refresh(
        @RequestBody refreshRequest: RefreshRequest
    ): AuthenticatedUserDto {
        return authService.refresh(refreshRequest.refreshToken).toAuthenticatedUserDto()
    }

    @PostMapping("/logout")
    fun logout(
        @RequestBody body: RefreshRequest
    ) {
        authService.logout(body.refreshToken)
    }

    @PostMapping("/resend-verification")
    @IpRateLimit(
        requests = 10,
        duration = 1L,
        unit = TimeUnit.HOURS
    )
    fun resendVerification(
        @Valid @RequestBody emailRequest: EmailRequest
    ) {
        emailRateLimiter.withRateLimit(emailRequest.email) {
            emailVerificationService.resendVerificationEmail(emailRequest.email)
        }
    }


    @GetMapping("/verify")
    fun verifyEmail(
        @RequestParam token: String
    ) {
        emailVerificationService.verifyEmail(token)
    }

    @PostMapping("/forgot-password")
    @IpRateLimit(
        requests = 10,
        duration = 1L,
        unit = TimeUnit.HOURS
    )
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
      @Valid @RequestBody changePasswordRequest: ChangePasswordRequest
    ) {
        passwordResetService.changePassword(
            userId = requestUserId,
            oldPassword = changePasswordRequest.oldPassword,
            newPassword = changePasswordRequest.newPassword,
        )
    }
}