package llc.bokadev.chirp.api.dto.ws

import com.fasterxml.jackson.annotation.JsonProperty
import llc.bokadev.chirp.domain.type.UserId

data class ProfilePictureUpdateDto(
    @JsonProperty("userId")
    val userId: UserId,
    @JsonProperty("newUrl")
    val newUrl: String?,
)
