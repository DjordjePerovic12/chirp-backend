package llc.bokadev.chirp.api.controllers

import jakarta.validation.Valid
import llc.bokadev.chirp.api.dto.ChatDto
import llc.bokadev.chirp.api.dto.CreateChatRequest
import llc.bokadev.chirp.api.mappers.toChatDto
import llc.bokadev.chirp.service.ChatService
import llc.bokadev.llc.bokadev.chirp.api.util.requestUserId
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
}

