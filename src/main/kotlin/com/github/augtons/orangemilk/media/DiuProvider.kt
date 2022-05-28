package com.github.augtons.orangemilk.media

import com.github.augtons.orangemilk.configurations.properties.BotAppProperties
import com.github.augtons.orangemilk.utils.httpDownloadFileSuspend
import com.github.augtons.orangemilk.utils.then
import org.springframework.stereotype.Service
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.io.path.Path

@Service
class DiuProvider(
    val botAppProperties: BotAppProperties,
) {

    suspend fun diu(code: String): File? {
        try {
            val validCode = code.toLongOrNull() ?: error("")
            val url = "http://api.klizi.cn/API/ce/diu.php?qq=$validCode"

            val cachePath = Path(botAppProperties.cacheDir, "diu")
            cachePath.toFile().mkdirs()
            val imgFile = Path(cachePath.toString(), "qq${validCode}_${System.currentTimeMillis()}.jpg").toFile()

            httpDownloadFileSuspend(url, imgFile)

            return imgFile.exists().then { imgFile }

        }catch (e: Exception){
            return null
        }
    }
}