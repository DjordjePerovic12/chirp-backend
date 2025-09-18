package llc.bokadev.chirp.api.mappers

import llc.bokadev.chirp.api.dto.AuthenticatedUserDto
import llc.bokadev.chirp.api.dto.UserDto
import llc.bokadev.chirp.domain.model.AuthenticatedUser
import llc.bokadev.chirp.domain.model.User

fun AuthenticatedUser.toAuthenticatedUserDto(): AuthenticatedUserDto {
    return AuthenticatedUserDto(
        user = user.toUserDto(),
        accessToken = accessToken,
        refreshToken = refreshToken
    )
}

fun User.toUserDto(): UserDto {
    return UserDto(
        id = id,
        email = email,
        username = username,
        hasVerifiedEmail = hasVerifiedEmail
    )
}