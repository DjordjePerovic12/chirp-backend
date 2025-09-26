package llc.bokadev.chirp.api.dto.ws

import com.fasterxml.jackson.annotation.JsonProperty
import llc.bokadev.chirp.domain.type.ChatId
import llc.bokadev.chirp.domain.type.ChatMessageId

data class SendMessageDto(
    @JsonProperty("messageId")
    val messageId: ChatMessageId? = null,
    @JsonProperty("chatId")
    val chatId: ChatId,
    @JsonProperty("content")
    val content: String
)
