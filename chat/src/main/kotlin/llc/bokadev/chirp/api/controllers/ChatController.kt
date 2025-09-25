package llc.bokadev.chirp.api.controllers

import jakarta.validation.Valid
import llc.bokadev.chirp.api.dto.AddParticipantToChatDto
import llc.bokadev.chirp.api.dto.ChatDto
import llc.bokadev.chirp.api.dto.ChatMessageDto
import llc.bokadev.chirp.api.dto.CreateChatRequest
import llc.bokadev.chirp.api.mappers.toChatDto
import llc.bokadev.chirp.service.ChatService
import llc.bokadev.chirp.api.util.requestUserId
import llc.bokadev.chirp.domain.type.ChatId
import llc.bokadev.chirp.service.ChatMessageService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@RestController
@RequestMapping("/api/chat")
class ChatController(private val chatService: ChatService) {

    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
    }

    @GetMapping("/{chatId}/messages")
    fun getMessagesForChat(
        @PathVariable("chatId") chatId: ChatId,
        @RequestParam("before", required = false) before: Instant? = null,
        @RequestParam("pageSize", required = false) pageSize: Int = DEFAULT_PAGE_SIZE,
    ): List<ChatMessageDto> {
        return chatService.getChatMessages(
            chatId = chatId,
            before = before,
            pageSize = pageSize
        )
    }

    @GetMapping("/{chatId}")
    fun getChat(
        @PathVariable("chatId") chatId: ChatId,
    ): ChatDto {
        return chatService.getChatById(
            chatId = chatId, requestUserId = requestUserId
        )?.toChatDto() ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }

    @GetMapping
    fun getChats(): List<ChatDto> {
        return chatService.findChatByUser(
            userId = requestUserId
        ).map { it.toChatDto() }
    }


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

