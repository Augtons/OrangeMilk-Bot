package com.github.augtons.orangemilk.configurations.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("media.music")
class MusicProperties {

    var kugou = KugouProperties()
    var netease = NetEaseProperties()

    /**
     * 酷狗
     */
    data class KugouProperties(
        var signatureGetter: String = ""
    )

    /**
     * 网易
     */
    data class NetEaseProperties(
        var apiUrl: String = ""
    )
}