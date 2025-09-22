package llc.bokadev.chirp.infra.database.repositories

import llc.bokadev.chirp.domain.type.UserId
import llc.bokadev.chirp.infra.database.entities.RefreshTokenEntity
import org.springframework.data.jpa.repository.JpaRepository

interface RefreshTokenRepository: JpaRepository<RefreshTokenEntity, Long> {

    fun findByUserIdAndHashedToken(userId: UserId, hashedToken: String): RefreshTokenEntity?

    fun deleteByUserIdAndHashedToken(userId: UserId, hashedToken: String): RefreshTokenEntity?

    fun deleteByUserId(userId: UserId)

}