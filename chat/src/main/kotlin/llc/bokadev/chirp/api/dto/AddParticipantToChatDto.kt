package llc.bokadev.chirp.api.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Size
import llc.bokadev.chirp.domain.type.UserId

data class AddParticipantToChatDto(
    @field:Size(min = 1)
    @JsonProperty("userIds")
    val userIds: List<UserId>
)
