package llc.bokadev.chirp.domain.exception

import java.lang.RuntimeException

class ChatNotFoundException : RuntimeException(
    "Chat not found"
)