package llc.bokadev.chirp.api.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank

data class ConfirmProfilePictureRequest(
    @field:NotBlank
    @JsonProperty("publicUrl")
    val publicUrl: String,

)
