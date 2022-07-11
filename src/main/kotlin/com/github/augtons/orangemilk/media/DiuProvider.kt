package com.github.augtons.orangemilk.media

import com.github.augtons.orangemilk.properties.BotAppProperties
import com.github.augtons.orangemilk.media.DiuProvider.Mode.*
import com.github.augtons.orangemilk.utils.httpDownloadFileSuspend
import com.github.augtons.orangemilk.utils.then
import org.springframework.stereotype.Service
import java.io.File
import kotlin.io.path.Path

@Service
class DiuProvider(
    val botAppProperties: BotAppProperties,
) {

    suspend fun diu(code: String, mode: Mode = DIU): File? {
        try {
            val validCode = code.toLongOrNull() ?: error("")
            val url = getUrl(validCode, mode)

            val cachePath = Path(botAppProperties.cacheDir, "diu")
            cachePath.toFile().mkdirs()
            val imgFile = Path(cachePath.toString(), "${mode}_qq${validCode}_${System.currentTimeMillis()}.jpg").toFile()

            httpDownloadFileSuspend(url, imgFile)

            return imgFile.exists().then { imgFile }

        }catch (e: Exception){
            return null
        }
    }


    private fun getUrl(code: Long, mode: Mode): String {
        return when(mode) {
            DIU -> "http://api.klizi.cn/API/ce/diu.php?qq=$code"
            PAI -> "https://api.xingzhige.com/API/paigua/?qq=$code"
            GRAB -> "https://api.xingzhige.com/API/grab/?qq=$code"
            BAO -> "https://api.xingzhige.com/API/baororo/?qq=$code"
            POUND -> "https://api.xingzhige.com/API/pound/?qq=$code"
        }
    }

    enum class Mode {
        DIU, //丢
        PAI, //拍瓜
        GRAB, //抓
        BAO, //抱
        POUND; //锤

        val label get() = when(this) {
            DIU -> "丢"
            PAI -> "拍"
            GRAB -> "抓"
            BAO -> "抱"
            POUND -> "锤"
        }
    }
}