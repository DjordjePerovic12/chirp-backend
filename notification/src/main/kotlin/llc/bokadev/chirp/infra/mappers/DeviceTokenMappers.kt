package llc.bokadev.chirp.infra.mappers

import llc.bokadev.chirp.domain.models.DeviceToken
import llc.bokadev.chirp.infra.database.DeviceTokenEntity

fun DeviceTokenEntity.toDeviceToken(): DeviceToken {
    return DeviceToken(
        userId = userId,
        token = token,
        id = id,
        platform = platform.toPlatformEntity(),
        createdAt = createdAt
    )
}