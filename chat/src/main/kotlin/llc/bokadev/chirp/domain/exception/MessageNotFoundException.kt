package llc.bokadev.chirp.domain.exception

import llc.bokadev.chirp.domain.type.ChatMessageId

class MessageNotFoundException(
    private val id: ChatMessageId
): RuntimeException(
    "Message with ID $id not found."
) {
}