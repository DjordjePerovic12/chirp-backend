package llc.bokadev.chirp.api.exception_handling

import llc.bokadev.chirp.domain.exception.ChatNotFoundException
import llc.bokadev.chirp.domain.exception.ChatParticipantNotFoundException
import llc.bokadev.chirp.domain.exception.InvalidChatSizeException
import llc.bokadev.chirp.domain.exception.MessageNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus

class ChatExceptionHandler {
    @ExceptionHandler(
        ChatNotFoundException::class,
        MessageNotFoundException::class,
        ChatParticipantNotFoundException::class
    )
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun onNotFound(e: Exception) = mapOf(
        "code" to "NOT_FOUND",
        "message" to e.message
    )

    @ExceptionHandler(
        InvalidChatSizeException::class
    )
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun onInvalidChatSize(e: InvalidChatSizeException) = mapOf(
        "code" to "INVALID_CHAT_SIZE",
        "message" to e.message
    )
}