package llc.bokadev.chirp.api.mappers

import llc.bokadev.chirp.api.dto.DeviceTokenDto
import llc.bokadev.chirp.api.dto.PlatformDto
import llc.bokadev.chirp.domain.models.DeviceToken

fun DeviceToken.toDeviceTokenDto(): DeviceTokenDto {
    return DeviceTokenDto(
        userId = userId,
        token = token,
        createdAt = createdAt,
    )
}

fun PlatformDto.toPlatformDto(): DeviceToken.Platform {
    return when(this) {
        PlatformDto.ANDROID -> DeviceToken.Platform.ANDROID
        PlatformDto.IOS -> DeviceToken.Platform.IOS
    }
}