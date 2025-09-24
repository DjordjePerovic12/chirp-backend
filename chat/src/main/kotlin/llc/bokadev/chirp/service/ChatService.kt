package llc.bokadev.chirp.service

import jakarta.transaction.Transactional
import llc.bokadev.chirp.domain.exception.ChatParticipantNotFoundException
import llc.bokadev.chirp.domain.exception.InvalidChatSizeException
import llc.bokadev.chirp.domain.models.Chat
import llc.bokadev.chirp.domain.type.UserId
import llc.bokadev.chirp.infra.database.ChatEntity
import llc.bokadev.chirp.infra.database.mappers.toChat
import llc.bokadev.chirp.infra.database.repositories.ChatParticipantRepository
import llc.bokadev.chirp.infra.database.repositories.ChatRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ChatService(
    private val chatRepository: ChatRepository,
    private val chatParticipantRepository: ChatParticipantRepository,
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
}