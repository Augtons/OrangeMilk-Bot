package com.github.augtons.orangemilk.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import kotlin.io.path.Path

@ConfigurationProperties("temp")
@Component
class TempProperties {
    var welcomeImgs: String = Path(System.getProperty("user.dir"), "welcomes").toString()
    var iotGroups: List<Long> = emptyList()
}