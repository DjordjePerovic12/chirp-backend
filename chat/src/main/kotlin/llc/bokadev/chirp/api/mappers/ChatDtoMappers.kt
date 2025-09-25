package llc.bokadev.chirp.api.mappers

import llc.bokadev.chirp.api.dto.ChatDto
import llc.bokadev.chirp.api.dto.ChatMessageDto
import llc.bokadev.chirp.api.dto.ChatParticipantDto
import llc.bokadev.chirp.domain.models.Chat
import llc.bokadev.chirp.domain.models.ChatMessage
import llc.bokadev.chirp.domain.models.ChatParticipant
import llc.bokadev.chirp.infra.database.ChatParticipantEntity


fun Chat.toChatDto(): ChatDto {
    return ChatDto(
        id = id,
        participants = participants.map { it.toChatParticipantDto() },
        lastActivityAt = lastActivityAt,
        lastMessage = lastMessage?.toChatMessageDto(),
        creator = creator.toChatParticipantDto()
    )
}

fun ChatMessage.toChatMessageDto(): ChatMessageDto {
    return ChatMessageDto(
        id = id,
        chatId = chatId,
        content = content,
        createdAt = createdAt,
        senderId = sender.userId
    )
}

fun ChatParticipant.toChatParticipantDto(): ChatParticipantDto {
    return ChatParticipantDto(
        userId = userId,
        username = username,
        email = email,
        profilePictureUrl = profilePictureUrl
    )
}

fun ChatParticipant.toChatParticipantEntity(): ChatParticipantEntity {
    return ChatParticipantEntity(
        userId = userId,
        username = username,
        email = email,
        profilePictureUrl = profilePictureUrl
    )
}