package llc.bokadev.chirp.domain.event

import llc.bokadev.chirp.domain.type.ChatId
import llc.bokadev.chirp.domain.type.ChatMessageId

data class MessageDeletedEvent(
    val messageId: ChatMessageId,
    val chatId: ChatId
)
