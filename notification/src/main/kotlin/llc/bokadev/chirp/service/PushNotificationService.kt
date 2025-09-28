package llc.bokadev.chirp.service

import jakarta.transaction.Transactional
import llc.bokadev.chirp.domain.exception.InvalidDeviceTokenException
import llc.bokadev.chirp.domain.models.DeviceToken
import llc.bokadev.chirp.domain.models.PushNotification
import llc.bokadev.chirp.domain.type.ChatId
import llc.bokadev.chirp.domain.type.UserId
import llc.bokadev.chirp.infra.database.DeviceTokenEntity
import llc.bokadev.chirp.infra.database.DeviceTokenRepository
import llc.bokadev.chirp.infra.mappers.toDeviceToken
import llc.bokadev.chirp.infra.mappers.toPlatformEntity
import llc.bokadev.chirp.infra.push_notification.FirebasePushNotificationService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class PushNotificationService(
    private val deviceTokenRepository: DeviceTokenRepository,
    private val firebasePushNotificationService: FirebasePushNotificationService
) {

    private val logger = LoggerFactory.getLogger(PushNotificationService::class.java.name)

    @Transactional
    fun registerDevice(
        userId: UserId,
        token: String,
        platform: DeviceToken.Platform
    ): DeviceToken {
        val existing = deviceTokenRepository.findByToken(token)

        val trimmedToken = token.trim()
        if (existing == null && !firebasePushNotificationService.isValidToken(trimmedToken)) {
            throw InvalidDeviceTokenException()
        }

        val entity = if (existing != null) {
            deviceTokenRepository.save(
                existing.apply {
                    this.userId = userId
                }
            )
        } else {
            deviceTokenRepository.save(
                DeviceTokenEntity(
                    userId = userId,
                    token = trimmedToken,
                    platform = platform.toPlatformEntity()
                )
            )
        }

        return entity.toDeviceToken()
    }

    @Transactional
    fun unregisterDevice(token: String) {
        deviceTokenRepository.deleteByToken(token.trim())
    }

    fun sendNewMessageNotifications(
        recipientUserIds: List<UserId>,
        senderUserId: UserId,
        senderUsername: String,
        message: String,
        chatId: ChatId
    ) {
        val deviceTokens = deviceTokenRepository.findByUserIdIn(recipientUserIds)
        if (deviceTokens.isEmpty()) {
            logger.info("No device tokens found for $recipientUserIds")
        }

        val recipients = deviceTokens
            .filter { it.userId != senderUserId }
            .map { it.toDeviceToken() }

        val notification = PushNotification(
            title = "New message from $senderUsername",
            recipients = recipients,
            message = message,
            chatId = chatId,
            data = mapOf(
                "chatId" to chatId.toString(),
                "type" to "new_message",
            )
        )

        firebasePushNotificationService.sendNotification(notification)
    }
}