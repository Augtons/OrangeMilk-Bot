package com.github.augtons.orangemilk.configurations.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import kotlin.io.path.Path

@Component
@ConfigurationProperties("media.sing")
class SingProperties {

    var path: String = Path(System.getProperty("user.dir"), "sings").toString()
}