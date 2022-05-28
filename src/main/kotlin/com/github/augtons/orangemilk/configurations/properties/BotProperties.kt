package com.github.augtons.orangemilk.configurations.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("bot")
class BotProperties {
    var qq: Long = 0L
    var password: String = ""

    var masters: List<Long> = emptyList()
}