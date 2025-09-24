package llc.bokadev.chirp.infra.database.repositories

import llc.bokadev.chirp.domain.type.UserId
import llc.bokadev.chirp.infra.database.ChatEntity
import llc.bokadev.chirp.domain.type.ChatId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ChatRepository : JpaRepository<ChatEntity, ChatId> {

    // So user can't fetch chats he doesn't belong into

    @Query("""
        SELECT c
        FROM ChatEntity c
        LEFT JOIN FETCH c.participants
        LEFT JOIN FETCH c.creator
        WHERE c.id = :id
        AND EXISTS (
            SELECT 1 
            FROM c.participants p 
            WHERE p.userId = :userId
            
        )
    """)
    fun findChatById(id: ChatId, userId: ChatId): ChatEntity?

    @Query("""
        SELECT c
        FROM ChatEntity c
        LEFT JOIN FETCH c.participants
        LEFT JOIN FETCH c.creator
        WHERE EXISTS (
        SELECT 1
        FROM c.participants p
        WHERE p.userId = :userId
        )
        
    """)
    fun findAllByUserId(userId: UserId): List<ChatEntity>
}