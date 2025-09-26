package llc.bokadev.chirp.api.dto.ws

import llc.bokadev.chirp.domain.type.ChatId

data class ChatParticipantsChangedDto(
    val chatId: ChatId
)
