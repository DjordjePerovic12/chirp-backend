package llc.bokadev.chirp.api.dto

import com.fasterxml.jackson.annotation.JsonProperty
import llc.bokadev.chirp.domain.type.UserId
import java.time.Instant

data class DeviceTokenDto(
    @JsonProperty("userId")
    val userId: UserId,
    @JsonProperty("token")
    val token: String,
    @JsonProperty("createdAt")
    val createdAt: Instant
)
