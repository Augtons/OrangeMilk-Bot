package com.github.augtons.orangemilk.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("game")
class GameProperties {

    var idiom = IdiomRepository()

    data class IdiomRepository(
        var repo: String = "",
        var repoCharset: String = "GBK"
    )
}