package llc.bokadev.chirp.domain.event

import llc.bokadev.chirp.domain.type.ChatId
import llc.bokadev.chirp.domain.type.UserId

data class ChatCreatedEvent(
    val chatId: ChatId,
    val participantIds: List<UserId>,
)
