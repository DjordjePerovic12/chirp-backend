package llc.bokadev.chirp.api.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class RefreshRequest(
    @JsonProperty(value = "refreshToken", required = true) val refreshToken: String,
)
