package llc.bokadev.chirp.service

import llc.bokadev.chirp.domain.type.ChatId
import org.springframework.cache.annotation.CacheEvict
import org.springframework.stereotype.Component

@Component
class MessageCacheEvictionHelper {

    @CacheEvict(
        value = ["messages"],
        key = "#chatId",

        )
    fun evictMessagesCache(chatId: ChatId) {
        // NO-OP: Let spring evict cache
    }
}