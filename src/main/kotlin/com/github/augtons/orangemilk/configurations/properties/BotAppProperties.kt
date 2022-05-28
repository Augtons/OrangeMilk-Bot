package com.github.augtons.orangemilk.configurations.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import kotlin.io.path.Path
import kotlin.io.path.name

@Component
@ConfigurationProperties("botapp", ignoreUnknownFields = true)
class BotAppProperties {

    /**
     *
     */
    var cacheDir = Path(System.getProperty("user.dir"), "app_cache").toString()

}