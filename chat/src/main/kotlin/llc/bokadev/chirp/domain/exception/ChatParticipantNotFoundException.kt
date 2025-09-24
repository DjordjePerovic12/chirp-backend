package llc.bokadev.chirp.domain.exception

import llc.bokadev.chirp.domain.type.UserId

class ChatParticipantNotFoundException(
    private val id: UserId,
) : RuntimeException(
    "The chat participant with the ID $id was not found."
) {
}