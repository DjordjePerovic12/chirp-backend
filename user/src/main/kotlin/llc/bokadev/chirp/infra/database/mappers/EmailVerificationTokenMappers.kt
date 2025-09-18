package llc.bokadev.chirp.infra.database.mappers

import llc.bokadev.chirp.domain.model.EmailVerificationToken
import llc.bokadev.chirp.infra.database.entities.EmailVerificationTokenEntity

fun EmailVerificationTokenEntity.toEmailVerificationToken(): EmailVerificationToken {
    return EmailVerificationToken(
        id = id,
        token = token,
        user = user.toUser()
    )
}