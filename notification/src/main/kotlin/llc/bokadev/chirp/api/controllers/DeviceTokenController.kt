package llc.bokadev.chirp.api.controllers

import jakarta.validation.Valid
import llc.bokadev.chirp.api.dto.DeviceTokenDto
import llc.bokadev.chirp.api.dto.RegisterDeviceRequest
import llc.bokadev.chirp.api.mappers.toDeviceTokenDto
import llc.bokadev.chirp.api.mappers.toPlatformDto
import llc.bokadev.chirp.api.util.requestUserId
import llc.bokadev.chirp.service.PushNotificationService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/notifications")
class DeviceTokenController(private val pushNotificationService: PushNotificationService) {

    @PostMapping("/register")
    fun registerDeviceToken(
        @Valid @RequestBody body: RegisterDeviceRequest
    ): DeviceTokenDto {
        return pushNotificationService.registerDevice(
            userId = requestUserId,
            token = body.token,
            platform = body.platform.toPlatformDto()
        ).toDeviceTokenDto()
    }

    @DeleteMapping("/{token}")
    fun unregisterDeviceToken(
        @PathVariable token: String
    ) {
        pushNotificationService.unregisterDevice(token)
    }
}