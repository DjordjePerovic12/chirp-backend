package llc.bokadev.chirp.infra.message_queue

import llc.bokadev.chirp.domain.events.user.UserEvent
import llc.bokadev.chirp.service.EmailService
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Duration


@Component
class NotificationUserEventListener(private val emailService: EmailService) {

    @RabbitListener(queues = [MessageQueues.NOTIFICATION_USER_EVENTS])
    @Transactional
    fun handleUserEvent(event: UserEvent) {
        when (event) {
            is UserEvent.Created -> {
                emailService.sendVerificationEmail(
                    email = event.email,
                    username = event.username,
                    userId = event.userId,
                    verificationToken = event.verificationToken,
                )
            }

            is UserEvent.RequestResendVerification -> {
                emailService.sendVerificationEmail(
                    email = event.email,
                    username = event.username,
                    userId = event.userId,
                    verificationToken = event.verificationToken,
                )
            }

            is UserEvent.RequestResetPassword -> {
                emailService.sendPasswordResetEmail(
                    email = event.email,
                    username = event.username,
                    userId = event.userId,
                    verificationToken = event.verificationToken,
                    expiresIn = Duration.ofMinutes(event.expiresInMinutes)

                )
            }

            else -> Unit
        }
    }
}