package llc.bokadev.chirp.domain.models

import llc.bokadev.chirp.domain.type.ChatId
import llc.bokadev.chirp.domain.type.ChatMessageId
import java.time.Instant

data class ChatMessage(
    val id: ChatMessageId,
    val chatId: ChatId,
    val sender: ChatParticipant,
    val content: String,
    val createdAt: Instant
)
