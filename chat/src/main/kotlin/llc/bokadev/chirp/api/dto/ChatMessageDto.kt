package llc.bokadev.chirp.api.dto

import llc.bokadev.chirp.domain.type.ChatId
import llc.bokadev.chirp.domain.type.ChatMessageId
import llc.bokadev.chirp.domain.type.UserId
import java.time.Instant

data class ChatMessageDto(
    val id: ChatMessageId,
    val chatId: ChatId,
    val content: String,
    val createdAt: Instant,
    val senderId: UserId
)
