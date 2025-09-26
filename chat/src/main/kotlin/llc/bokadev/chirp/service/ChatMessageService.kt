package llc.bokadev.chirp.service

import jakarta.transaction.Transactional
import llc.bokadev.chirp.api.dto.ChatMessageDto
import llc.bokadev.chirp.api.mappers.toChatMessageDto
import llc.bokadev.chirp.domain.event.MessageDeletedEvent
import llc.bokadev.chirp.domain.exception.ChatNotFoundException
import llc.bokadev.chirp.domain.exception.ChatParticipantNotFoundException
import llc.bokadev.chirp.domain.exception.MessageNotFoundException
import llc.bokadev.chirp.domain.exceptions.ForbiddenException
import llc.bokadev.chirp.domain.models.ChatMessage
import llc.bokadev.chirp.domain.type.ChatId
import llc.bokadev.chirp.domain.type.ChatMessageId
import llc.bokadev.chirp.domain.type.UserId
import llc.bokadev.chirp.infra.database.ChatMessageEntity
import llc.bokadev.chirp.infra.database.mappers.toChatMessage
import llc.bokadev.chirp.infra.database.repositories.ChatMessageRepository
import llc.bokadev.chirp.infra.database.repositories.ChatParticipantRepository
import llc.bokadev.chirp.infra.database.repositories.ChatRepository
import llc.bokadev.chirp.infra.message_queue.EventPublisher
import llc.bokadev.chirp.domain.events.chat.ChatEvent
import org.springframework.cache.annotation.CacheEvict
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class ChatMessageService(
    private val chatMessageRepository: ChatMessageRepository,
    private val chatRepository: ChatRepository,
    private val chatParticipantRepository: ChatParticipantRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val eventPublisher: EventPublisher
) {
    @Transactional
    @CacheEvict(
        value = ["messages"],
        key = "#chatId",

        )
    fun sendMessage(
        chatId: ChatId,
        senderId: UserId,
        content: String,
        messageId: ChatMessageId? = null
    ): ChatMessage {
        val chat = chatRepository.findChatById(chatId, senderId)
            ?: throw ChatNotFoundException()
        val sender = chatParticipantRepository.findByIdOrNull(senderId)
            ?: throw ChatParticipantNotFoundException(senderId)

        val savedMessage = chatMessageRepository.saveAndFlush(
            ChatMessageEntity(
                id = messageId,
                content = content.trim(),
                chatId = chatId,
                chat = chat,
                sender = sender,
            )
        )

        eventPublisher.publish(
            ChatEvent.NewMessage(
                senderId = sender.userId,
                senderUsername = sender.username,
                recipientIds = chat.participants.map { it.userId }.toSet(),
                chatId = chatId,
                message = savedMessage.content
            )
        )

        return savedMessage.toChatMessage()
    }

    @Transactional
    fun deleteMessage(
        messageId: ChatMessageId,
        requestUserId: UserId
    ) {
        val message = chatMessageRepository.findByIdOrNull(messageId)
            ?: throw MessageNotFoundException(messageId)

        if (message.sender.userId != requestUserId) {
            throw ForbiddenException()
        }

        chatMessageRepository.delete(message)

        applicationEventPublisher.publishEvent(
            MessageDeletedEvent(
                chatId = message.chatId,
                messageId = messageId
            )
        )

        evictMessagesCache(message.chatId)
    }

    @CacheEvict(
        value = ["messages"],
        key = "#chatId",

        )
    fun evictMessagesCache(chatId: ChatId) {
        // NO-OP: Let spring evict cache
    }
}