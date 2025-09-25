package llc.bokadev.chirp.service

import jakarta.transaction.Transactional
import llc.bokadev.chirp.api.dto.ChatDto
import llc.bokadev.chirp.api.dto.ChatMessageDto
import llc.bokadev.chirp.api.mappers.toChatMessageDto
import llc.bokadev.chirp.domain.event.ChatParticipantLeftEvent
import llc.bokadev.chirp.domain.event.ChatParticipantsJoinedEvent
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
import org.springframework.cache.annotation.Cacheable
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class ChatService(
    private val chatRepository: ChatRepository,
    private val chatParticipantRepository: ChatParticipantRepository,
    private val chatMessageRepository: ChatMessageRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {


    @Cacheable(
        value = ["messages"],
        key = "#chatId",
        condition = "#before == null&& #pageSize <= 50",
        sync = true
    )
    fun getChatMessages(
        chatId: ChatId,
        before: Instant?,
        pageSize: Int
    ): List<ChatMessageDto> {
        return chatMessageRepository.findByChatIdBefore(
            chatId = chatId,
            before = before ?: Instant.now(),
            pageable = PageRequest.of(0, pageSize)
        )
            .content
            .asReversed()
            .map { it.toChatMessage().toChatMessageDto() }
    }

    fun getChatById(
        chatId: ChatId,
        requestUserId: UserId
    ): Chat? {
        return chatRepository.findChatById(
            chatId, requestUserId
        )?.toChat(lastMessageForChat(chatId))
    }

    fun findChatByUser(userId: UserId): List<Chat> {
        val chatEntities = chatRepository.findAllByUserId(userId)
        val chatIds = chatEntities.mapNotNull { it.id }
        val latestMessages = chatMessageRepository.findLatestMessagesByChatIds(chatIds.toSet())
            .associateBy { it.chatId }
        return chatEntities.map {
            it.toChat(
                lastMessage = latestMessages[it.id]?.toChatMessage()
            )
        }
            .sortedByDescending { it.lastActivityAt }
    }

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

        applicationEventPublisher.publishEvent(
            ChatParticipantsJoinedEvent(
                chatId = chatId,
                userIds = userIds
            )
        )

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
        if (newParticipantsSize == 0) {
            chatRepository.deleteById(chatId)
            return
        }

        chatRepository.save(
            chat.apply {
                this.participants = chat.participants - participant
            }
        )

        applicationEventPublisher.publishEvent(
            ChatParticipantLeftEvent(
                userId = userId,
                chatId = chatId,
            )
        )
    }

    private fun lastMessageForChat(chatId: ChatId): ChatMessage? {
        return chatMessageRepository
            .findLatestMessagesByChatIds(setOf(chatId))
            .firstOrNull()
            ?.toChatMessage()
    }
}