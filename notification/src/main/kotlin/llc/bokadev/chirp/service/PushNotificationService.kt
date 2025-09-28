package llc.bokadev.chirp.service

import com.fasterxml.jackson.annotation.JsonProperty
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
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentSkipListMap

@Service
class PushNotificationService(
    private val deviceTokenRepository: DeviceTokenRepository,
    private val firebasePushNotificationService: FirebasePushNotificationService
) {

    companion object {
        private val RETRY_DELAYS_SECONDS = listOf(
            30L,
            60L,
            120L,
            300L,
            600L
        )
        const val MAX_RETRY_AGE_MINUTES = 30L
    }

    private val retryQueue = ConcurrentSkipListMap<Long, MutableList<RetryData>>()

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

        sendWithRetry(notification = notification)
    }

    fun sendWithRetry(
        notification: PushNotification,
        attempt: Int = 0,
    ) {
        val result = firebasePushNotificationService.sendNotification(notification)

        result.permanentFailures.forEach {
            deviceTokenRepository.deleteByToken(it.token)
        }

        if (result.temporaryFailures.isNotEmpty() && attempt < RETRY_DELAYS_SECONDS.size) {
            val retryNotification = notification.copy(
                recipients = result.temporaryFailures
            )
            scheduleRetry(retryNotification, attempt + 1)
        }

        if (result.succeeded.isNotEmpty()) {
            logger.info("Successfully sent message to ${result.succeeded.size} devices")
        }
    }

    private fun scheduleRetry(
        notification: PushNotification,
        attempt: Int,
    ) {
        val delay = RETRY_DELAYS_SECONDS.getOrElse(attempt - 1) {
            RETRY_DELAYS_SECONDS.last()
        }

        val executeAt = Instant.now().plusSeconds(delay)
        val executeAtMillis = executeAt.toEpochMilli()

        val retryData = RetryData(
            notification = notification,
            attempt = attempt,
            createdAt = Instant.now()
        )

        retryQueue.compute(executeAtMillis) { _, retries ->
            (retries ?: mutableListOf()).apply {
                add(retryData)
            }
        }

        logger.info("Scheduled retry $attempt for ${notification.id} in $delay seconds")
    }

    @Scheduled(fixedDelay = 15_000L)
    fun processRetries() {
        val now = Instant.now()
        val nowMillis = now.toEpochMilli()

        val toProcess = retryQueue.headMap(nowMillis, true)

        if (toProcess.isEmpty()) {
            return
        }

        val entries = toProcess.entries.toList()
        entries.forEach { (timeMillis, retires) ->
            retryQueue.remove(timeMillis)

            retires.forEach { retry ->
                try {
                    val age = Duration.between(retry.createdAt, now)
                    if (age.toMinutes() > MAX_RETRY_AGE_MINUTES) {
                        logger.warn("Dropping old retry (${age.toMinutes()} old ")
                        return@forEach
                    }

                    sendWithRetry(retry.notification, retry.attempt)
                } catch (e: Exception) {
                    logger.warn("Error processing retry ${retry.notification.id}", e)
                }
            }
        }
    }


    private data class RetryData(
        @JsonProperty("notification")
        val notification: PushNotification,
        @JsonProperty("attempt")
        val attempt: Int,
        @JsonProperty("createdAt")
        val createdAt: Instant
    )
}