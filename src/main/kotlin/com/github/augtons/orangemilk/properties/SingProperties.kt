package com.github.augtons.orangemilk.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import kotlin.io.path.Path

/**
 * 媒体：sing的配置
 * ```ymal
 * media:
 *   sing:
 *     path: /path/to/sings #歌曲的目录，默认: ./sings
 * ```
 */
@Component
@ConfigurationProperties("media.sing")
class SingProperties {

    var path: String = Path(System.getProperty("user.dir"), "sings").toString()
}