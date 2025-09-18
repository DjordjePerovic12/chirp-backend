package llc.bokadev.chirp.domain.exception

import java.lang.RuntimeException

class UserAlreadyExistsException : RuntimeException(
    "User with this email or username already exists"
)