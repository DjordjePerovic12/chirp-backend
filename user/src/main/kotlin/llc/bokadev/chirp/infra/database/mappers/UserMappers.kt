package llc.bokadev.chirp.infra.database.mappers

import llc.bokadev.chirp.domain.model.User
import llc.bokadev.chirp.infra.database.entities.UserEntity

fun UserEntity.toUser(): User {
    return User(
        id = id!!,
        username = username,
        email = email,
        hasVerifiedEmail = hasVerifiedEmail
    )
}