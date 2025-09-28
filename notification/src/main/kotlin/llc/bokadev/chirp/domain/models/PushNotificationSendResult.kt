package llc.bokadev.chirp.domain.models

data class PushNotificationSendResult(
    val succeeded: List<DeviceToken>,
    val temporaryFailures: List<DeviceToken>,
    val permanentFailures: List<DeviceToken>
)
