package llc.bokadev.chirp.domain.exceptions

import java.lang.RuntimeException

class InvalidTokenException(
    override val message: String?
) : RuntimeException(
    message ?: "Invalid token"
)