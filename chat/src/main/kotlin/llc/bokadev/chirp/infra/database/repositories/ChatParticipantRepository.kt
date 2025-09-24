package llc.bokadev.chirp.infra.database.repositories

import llc.bokadev.chirp.domain.type.UserId
import llc.bokadev.chirp.infra.database.ChatParticipantEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ChatParticipantRepository : JpaRepository<ChatParticipantEntity, UserId> {
    fun findByUserIdIn(userIds: Set<UserId>) : Set<ChatParticipantEntity>
    @Query("""
        SELECT p 
FROM ChatParticipantEntity p
WHERE LOWER(p.username) = :query OR LOWER(p.email) = :query
    """)
    fun findByEmailOrUsername(query: String): ChatParticipantEntity?
}