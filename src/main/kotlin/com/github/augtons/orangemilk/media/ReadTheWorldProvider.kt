package com.github.augtons.orangemilk.media

import com.github.augtons.orangemilk.properties.BotAppProperties
import com.github.augtons.orangemilk.utils.httpDownloadFileSuspend
import com.github.augtons.orangemilk.utils.httpGetString
import com.google.gson.Gson
import org.springframework.stereotype.Service
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.io.path.Path

@Service
class ReadTheWorldProvider(
    val botAppProperties: BotAppProperties
) {

    data class ResultJson(
        val imageUrl: String,
        val datatime: String
    )

    suspend fun getImage(): File? {
        // yyyy-MM-dd
        val dateString: String = LocalDateTime.now().format(
            DateTimeFormatter.ISO_LOCAL_DATE
        )
        val cachePath = Path(botAppProperties.cacheDir, "readTheWorld").toString()
        val imgFile = File(Path(cachePath, "${dateString}.png").toString())
        File(cachePath).mkdirs()

        if (imgFile.exists() && LocalDateTime.now().hour >= 5) { // 文件存在，且为今日，直接发
            return imgFile
        }
        // 文件不存在，获取图片并保存
        val jsonString = httpGetString("https://api.03c3.cn/zb/api.php")

        val json = Gson().fromJson(jsonString, ResultJson::class.java)

        if (json.datatime != dateString) {
            return null
        }

        httpDownloadFileSuspend(json.imageUrl, imgFile)
        return imgFile

    }
}