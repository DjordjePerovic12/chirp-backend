package llc.bokadev.chirp.infra.database.mappers

import llc.bokadev.chirp.domain.models.Chat
import llc.bokadev.chirp.domain.models.ChatMessage
import llc.bokadev.chirp.domain.models.ChatParticipant
import llc.bokadev.chirp.infra.database.ChatEntity
import llc.bokadev.chirp.infra.database.ChatParticipantEntity

fun ChatEntity.toChat(lastMessage: ChatMessage? = null): Chat {
    return Chat(
        id = id!!,
        participants = participants.map {
            it.toChatParticipant()
        }.toSet(),
        creator = creator.toChatParticipant(),
        lastActivityAt = lastMessage?.createdAt ?: createdAt,
        createdAt = createdAt,
        lastMessage = lastMessage

    )
}

fun ChatParticipantEntity.toChatParticipant(): ChatParticipant {
    return ChatParticipant(
        userId = userId,
        username = username,
        email = email,
        profilePictureUrl = profilePictureUrl
    )
}