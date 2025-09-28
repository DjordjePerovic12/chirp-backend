package llc.bokadev.chirp.domain.event

import llc.bokadev.chirp.domain.type.UserId

data class ProfilePictureUpdatedEvent(
    val userId: UserId,
    val newUrl: String?
)
