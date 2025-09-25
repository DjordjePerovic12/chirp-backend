package llc.bokadev.chirp.domain.exceptions

class ForbiddenException : RuntimeException(
    "You don't have appropriate permissions to perform this action."
)