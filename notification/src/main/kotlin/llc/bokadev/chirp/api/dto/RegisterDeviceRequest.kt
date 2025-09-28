package llc.bokadev.chirp.api.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank

data class RegisterDeviceRequest(
    @JsonProperty("token")
    @field:NotBlank
    val token: String,
    @JsonProperty("platform")
    val platform: PlatformDto
)

enum class PlatformDto {
    ANDROID, IOS
}
