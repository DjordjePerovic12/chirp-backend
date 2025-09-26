package llc.bokadev.chirp.api.dto.ws

import com.fasterxml.jackson.annotation.JsonProperty

enum class IncomingWebSocketMessageType {
    NEW_MESSAGE

}

enum class OutgoingWebSocketMessageType {
    NEW_MESSAGE,
    MESSAGE_DELETED,
    PROFILE_PICTURE_UPDATED,
    CHAT_PARTICIPANTS_CHANGED,
    ERROR
}

data class IncomingWebSocketMessage(
    @JsonProperty("type")
    val type: IncomingWebSocketMessageType,
    @JsonProperty("payload")
    val payload: String
)

data class OutgoingWebSocketMessage(
    @JsonProperty("type")
    val type: OutgoingWebSocketMessageType,
    @JsonProperty("payload")
    val payload: String
)