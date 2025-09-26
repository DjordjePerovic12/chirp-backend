package llc.bokadev.chirp.api.dto.ws

import llc.bokadev.chirp.domain.type.ChatId
import llc.bokadev.chirp.domain.type.ChatMessageId

data class DeleteMessageDto(
    val chatId: ChatId,
    val messageId: ChatMessageId
)
