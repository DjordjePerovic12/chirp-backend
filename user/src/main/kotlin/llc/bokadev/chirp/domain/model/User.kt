package llc.bokadev.chirp.domain.model

import llc.bokadev.chirp.domain.type.UserId

data class User(
    val id: UserId,
    val username: String,
    val email: String,
    val hasVerifiedEmail: Boolean
)
