package llc.bokadev.llc.bokadev.chirp.api.util

import llc.bokadev.chirp.domain.type.UserId
import llc.bokadev.llc.bokadev.chirp.domain.exceptions.UnauthorizedException
import org.springframework.security.core.context.SecurityContextHolder


val requestUserId: UserId
    get() = SecurityContextHolder.getContext().authentication?.principal as? UserId
        ?: throw UnauthorizedException()