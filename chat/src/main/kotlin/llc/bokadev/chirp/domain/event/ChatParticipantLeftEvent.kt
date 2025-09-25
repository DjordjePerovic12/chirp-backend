package llc.bokadev.chirp.domain.event

import llc.bokadev.chirp.domain.type.ChatId
import llc.bokadev.chirp.domain.type.UserId

data class ChatParticipantLeftEvent(
    val chatId: ChatId,
    val userId: UserId
)
