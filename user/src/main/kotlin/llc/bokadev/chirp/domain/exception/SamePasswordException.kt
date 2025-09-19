package llc.bokadev.chirp.domain.exception

class SamePasswordException: RuntimeException(
    "New password can't be same as the old password."
)