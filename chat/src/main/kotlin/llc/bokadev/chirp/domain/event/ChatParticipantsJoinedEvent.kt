package llc.bokadev.chirp.domain.event

import llc.bokadev.chirp.domain.type.ChatId
import llc.bokadev.chirp.domain.type.UserId

data class ChatParticipantsJoinedEvent(
    val chatId: ChatId,
    val userIds: Set<UserId>
)
