package llc.bokadev.chirp.api.controllers

import jakarta.validation.Valid
import llc.bokadev.chirp.api.dto.AddParticipantToChatDto
import llc.bokadev.chirp.api.dto.ChatDto
import llc.bokadev.chirp.api.dto.CreateChatRequest
import llc.bokadev.chirp.api.mappers.toChatDto
import llc.bokadev.chirp.service.ChatService
import llc.bokadev.chirp.api.util.requestUserId
import llc.bokadev.chirp.domain.type.ChatId
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/chat")
class ChatController(private val chatService: ChatService) {

    @PostMapping
    fun createChat(
        @Valid @RequestBody body: CreateChatRequest,
    ): ChatDto {
        return chatService.createChat(
            creatorId = requestUserId,
            otherUsersId = body.otherUserIds.toSet()
        ).toChatDto()
    }

    @PostMapping("/{chatId}/add")
    fun addChatParticipants(
        @PathVariable chatId: ChatId,
        @Valid @RequestBody body: AddParticipantToChatDto
    ): ChatDto {
        return chatService.addParticipantsToChat(
            requestUserId = requestUserId,
            chatId = chatId,
            userIds = body.userIds.toSet()
        ).toChatDto()
    }

    @DeleteMapping("/{chatId}/leave")
    fun leaveChat(
        @PathVariable chatId: ChatId
    ) {
        chatService.removeParticipantsFromChat(
            chatId = chatId,
            userId = requestUserId,
        )
    }
}

