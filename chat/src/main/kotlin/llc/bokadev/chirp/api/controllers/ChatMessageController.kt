package llc.bokadev.chirp.api.controllers

import jdk.internal.joptsimple.internal.Messages.message
import llc.bokadev.chirp.api.util.requestUserId
import llc.bokadev.chirp.domain.models.ChatMessage
import llc.bokadev.chirp.domain.type.ChatMessageId
import llc.bokadev.chirp.service.ChatMessageService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/aoi/messages")
class ChatMessageController(
    private val chatMessageService: ChatMessageService
) {
    @DeleteMapping("/{messageId}")
    fun deleteMessage(
        @PathVariable messageId: ChatMessageId
    ) {
        chatMessageService.deleteMessage(messageId, requestUserId)
    }
}