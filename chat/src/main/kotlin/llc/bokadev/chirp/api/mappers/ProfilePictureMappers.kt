package llc.bokadev.chirp.api.mappers

import llc.bokadev.chirp.api.dto.PictureUploadResponse
import llc.bokadev.chirp.domain.models.ProfilePictureUploadCredentials

fun ProfilePictureUploadCredentials.toResponse(): PictureUploadResponse {
    return PictureUploadResponse(
        uploadUrl = uploadUrl,
        headers = headers,
        expiresAt = expiresAt,
        publicUrl = publicUrl
    )
}