package llc.bokadev.chirp.api.dto.ws

import llc.bokadev.chirp.domain.type.ChatId
import llc.bokadev.chirp.domain.type.ChatMessageId

data class SendMessageDto(
    val messageId: ChatMessageId? = null,
    val chatId: ChatId,
    val content: String
)
