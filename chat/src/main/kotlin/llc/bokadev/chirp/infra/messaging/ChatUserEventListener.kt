package llc.bokadev.chirp.infra.messaging

import llc.bokadev.chirp.domain.events.user.UserEvent
import llc.bokadev.chirp.domain.models.ChatParticipant
import llc.bokadev.chirp.infra.message_queue.MessageQueues
import llc.bokadev.chirp.service.ChatParticipantService
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class ChatUserEventListener(
    private val chatParticipantService: ChatParticipantService
) {

    @RabbitListener(queues = [MessageQueues.CHAT_USER_EVENTS])
    fun handleUserEvent(event: UserEvent) {
        when (event) {
            is UserEvent.Verified -> {
                chatParticipantService.createChatParticipant(
                    chatParticipant = ChatParticipant(
                        userId = event.userId,
                        email = event.email,
                        username = event.username,
                        profilePictureUrl = null
                    )
                )
            }

            else -> Unit
        }
    }
}