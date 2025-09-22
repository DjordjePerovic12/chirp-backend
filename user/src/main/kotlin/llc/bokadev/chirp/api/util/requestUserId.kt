package llc.bokadev.chirp.api.util

import llc.bokadev.chirp.domain.exception.UnauthorizedException
import llc.bokadev.chirp.domain.type.UserId
import org.springframework.security.core.context.SecurityContextHolder


val requestUserId: UserId
    get() = SecurityContextHolder.getContext().authentication?.principal as? UserId
        ?: throw UnauthorizedException()