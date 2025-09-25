package llc.bokadev.chirp.service

import jakarta.transaction.Transactional
import llc.bokadev.chirp.domain.exception.ChatNotFoundException
import llc.bokadev.chirp.domain.exception.ChatParticipantNotFoundException
import llc.bokadev.chirp.domain.exception.InvalidChatSizeException
import llc.bokadev.chirp.domain.exceptions.ForbiddenException
import llc.bokadev.chirp.domain.models.Chat
import llc.bokadev.chirp.domain.models.ChatMessage
import llc.bokadev.chirp.domain.type.ChatId
import llc.bokadev.chirp.domain.type.UserId
import llc.bokadev.chirp.infra.database.ChatEntity
import llc.bokadev.chirp.infra.database.ChatParticipantEntity
import llc.bokadev.chirp.infra.database.mappers.toChat
import llc.bokadev.chirp.infra.database.mappers.toChatMessage
import llc.bokadev.chirp.infra.database.repositories.ChatMessageRepository
import llc.bokadev.chirp.infra.database.repositories.ChatParticipantRepository
import llc.bokadev.chirp.infra.database.repositories.ChatRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ChatService(
    private val chatRepository: ChatRepository,
    private val chatParticipantRepository: ChatParticipantRepository,
    private val chatMessageRepository: ChatMessageRepository,
) {
    @Transactional
    fun createChat(
        creatorId: UserId,
        otherUsersId: Set<UserId>
    ): Chat {
        val otherParticipants = chatParticipantRepository.findByUserIdIn(
            userIds = otherUsersId
        )

        val allParticipants = (otherParticipants + creatorId)
        if (allParticipants.size < 2) {
            throw InvalidChatSizeException()
        }

        val creator = chatParticipantRepository.findByIdOrNull(creatorId)
            ?: throw ChatParticipantNotFoundException(creatorId)

        return chatRepository.save(
            ChatEntity(
                creator = creator,
                participants = setOf(creator) + otherParticipants
            )
        ).toChat(lastMessage = null)
    }

    @Transactional
    fun addParticipantsToChat(
        requestUserId: UserId,
        chatId: ChatId,
        userIds: Set<UserId>
    ): Chat {
        val chat = chatRepository.findByIdOrNull(chatId)
            ?: throw ChatNotFoundException()

        val isRequestingUserInChat = chat.participants.any {
            it.userId == requestUserId
        }

        if (!isRequestingUserInChat) {
            throw ForbiddenException()
        }

        val users = userIds.map { userId ->
            chatParticipantRepository.findByIdOrNull(userId)
                ?: ChatParticipantNotFoundException(userId)
        }

        val lastMessage = lastMessageForChat(chatId)
        val updatedChat = chatRepository.save(
            chat.apply {
                this.participants = (chat.participants + users) as Set<ChatParticipantEntity>
            }
        ).toChat(lastMessage)

        return updatedChat
    }

    @Transactional
    fun removeParticipantsFromChat(
        chatId: ChatId,
        userId: UserId,
    ) {
        val chat = chatRepository.findByIdOrNull(chatId)
            ?: throw ChatNotFoundException()

        val participant = chat.participants.find { it.userId == userId }
            ?: throw ChatParticipantNotFoundException(userId)

        val newParticipantsSize = chat.participants.size - 1
        if(newParticipantsSize == 0) {
            chatRepository.deleteById(chatId)
            return
        }

        chatRepository.save(
            chat.apply {
                this.participants = chat.participants - participant
            }
        )
    }

    private fun lastMessageForChat(chatId: ChatId): ChatMessage? {
        return chatMessageRepository
            .findLatestMessagesByChatIds(setOf(chatId))
            .firstOrNull()
            ?.toChatMessage()
    }
}