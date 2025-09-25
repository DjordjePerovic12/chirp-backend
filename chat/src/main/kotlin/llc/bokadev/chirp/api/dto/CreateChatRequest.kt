package llc.bokadev.chirp.api.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Size
import llc.bokadev.chirp.domain.type.UserId

data class CreateChatRequest(
    @field:Size(min = 1,
        message = "Chats must have at least 2 unique participants.")
    @JsonProperty("otherUserIds")
    val otherUserIds: List<UserId>,
)
