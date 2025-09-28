package llc.bokadev.chirp.api.controllers

import jakarta.validation.Valid
import llc.bokadev.chirp.api.dto.ChatParticipantDto
import llc.bokadev.chirp.api.dto.ConfirmProfilePictureRequest
import llc.bokadev.chirp.api.dto.PictureUploadResponse
import llc.bokadev.chirp.api.mappers.toChatParticipantDto
import llc.bokadev.chirp.api.mappers.toResponse
import llc.bokadev.chirp.service.ChatParticipantService
import llc.bokadev.chirp.api.util.requestUserId
import llc.bokadev.chirp.service.ProfilePictureService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/participants")
class ChatParticipantController(
    private val chatParticipantService: ChatParticipantService,
    private val profilePictureService: ProfilePictureService
) {

    @GetMapping
    fun getChatParticipantByUsernameOrEmail(
        @RequestParam(required = false) query: String?
    ): ChatParticipantDto {
        val participant = if (query == null) {
            chatParticipantService.findChatParticipantById(requestUserId)
        } else {
            chatParticipantService.findChatParticipantByEmailOrUsername(query)
        }
        return participant?.toChatParticipantDto() ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "ChatParticipant not found"
        )
    }

    @PostMapping("/profile-picture-upload")
    fun getProfilePictureUploadUrl(
        @RequestParam mimeType: String,
    ): PictureUploadResponse {
        return profilePictureService.generateUploadCredentials(
            userId = requestUserId,
            mimeType = mimeType
        ).toResponse()
    }

    @PostMapping("/confirm-profile-picture")
    fun confirmProfilePictureUpload(
        @Valid @RequestBody body: ConfirmProfilePictureRequest
    ) {
        profilePictureService.confirmProfilePictureUpload(
            userId = requestUserId,
            publicUrl = body.publicUrl,
        )
    }

    @DeleteMapping("/profile-picture")
    fun deleteProfilePicture() {
        profilePictureService.deleteProfilePicture(
            userId = requestUserId
        )
    }
}