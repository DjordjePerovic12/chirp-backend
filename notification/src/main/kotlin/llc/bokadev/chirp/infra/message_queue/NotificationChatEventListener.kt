package llc.bokadev.chirp.infra.message_queue

import llc.bokadev.chirp.domain.events.chat.ChatEvent
import llc.bokadev.chirp.domain.events.user.UserEvent
import llc.bokadev.chirp.service.EmailService
import llc.bokadev.chirp.service.PushNotificationService
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Duration


@Component
class NotificationChatEventListener(private val pushNotificationService: PushNotificationService) {

    @RabbitListener(queues = [MessageQueues.NOTIFICATION_CHAT_EVENTS])
    @Transactional
    fun handleUserEvent(event: ChatEvent) {
        when (event) {
            is ChatEvent.NewMessage -> {
                pushNotificationService.sendNewMessageNotifications(
                    recipientUserIds = event.recipientIds.toList(),
                    senderUserId = event.senderId,
                    senderUsername = event.senderUsername,
                    message = event.message,
                    chatId = event.chatId
                )
            }

            else -> Unit
        }
    }
}