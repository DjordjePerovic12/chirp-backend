package llc.bokadev.chirp.service

import llc.bokadev.chirp.api.mappers.toChatParticipantEntity
import llc.bokadev.chirp.domain.models.ChatParticipant
import llc.bokadev.chirp.domain.type.UserId
import llc.bokadev.chirp.infra.database.mappers.toChatParticipant
import llc.bokadev.chirp.infra.database.repositories.ChatParticipantRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ChatParticipantService(
    private val chatParticipantRepository: ChatParticipantRepository
) {

    fun createChatParticipant(
        chatParticipant: ChatParticipant
    ) {
        chatParticipantRepository.save(
            chatParticipant.toChatParticipantEntity()
        )
    }

    fun findChatParticipantById(id: UserId): ChatParticipant? {
        return chatParticipantRepository.findByIdOrNull(id)?.toChatParticipant()
    }

    fun findChatParticipantByEmailOrUsername(
        query: String
    ): ChatParticipant? {
        val normalizedQuery = query.lowercase().trim()

        return chatParticipantRepository.findByEmailOrUsername(
            query = normalizedQuery
        )?.toChatParticipant()
    }
}