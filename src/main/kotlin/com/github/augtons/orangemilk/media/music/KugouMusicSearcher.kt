package com.github.augtons.orangemilk.media.music

import com.github.augtons.orangemilk.properties.MusicProperties
import com.github.augtons.orangemilk.utils.httpGetString
import com.github.augtons.orangemilk.utils.logger
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import java.net.URLEncoder
import javax.annotation.PostConstruct

/**
 * ## KugouMusicSearcher
 * ### 功能：
 *  - 用于酷狗音乐搜歌
 *
 * ### 示例：
 * ```
 * val sing = kugouMusicSearcher.search("锦零 鹿鸣呦呦")
 * runBlocking {
 *     sing?.let {
 *         bot.getFriend(it)?.sendMessage(
 *             MusicShare(MusicKind.KugouMusic, sing.musicName, sing.singerName, sing.playURL, sing.imgURL, sing.playURL)
 *         )
 *     }
 * }
 ```
 */
@Service
class KugouMusicSearcher(
    val musicProperties: MusicProperties
) : MusicSearcher() {

    private val logger = logger<KugouMusicSearcher>()

    val enabled: Boolean by lazy { musicProperties.kugou.signatureGetter.isNotBlank() }

    @PostConstruct
    fun init() {
        if (!enabled) {
//            throw IllegalStateException("未指定酷狗音乐signature生成脚本命令")
            logger.warn("未指定酷狗音乐signature生成脚本命令, KugouMusicSearcher.enabled = $enabled")
        } else {
            logger.info("酷狗搜歌已启用 KugouMusicSearcher.enabled = $enabled")
        }
    }

    /**
     * 搜索获取全部结果的第一首歌
     */
    override fun search(keyword: String): MusicResult? = runBlocking {
        searchSuspend(keyword)
    }

    suspend fun searchSuspend(keyword: String): MusicResult? {
        try {
            val sings = getMusicLists(keyword)
            for (sing in sings) {
                try {
                    return getMusic(sing)
                } catch (_e: Exception) {
                    continue
                }
            }
            throw IllegalStateException("酷狗点歌：未找到“$keyword”")
        } catch (e: Exception) {
            logger.error(e.message)
            return null
        }
    }

    /**
     * 酷狗音乐的MD5校验
     */
    fun getMd5(keyword: String): String {
        if (!enabled) { return "" }

        val signatureProcess = Runtime.getRuntime().exec(
            "${musicProperties.kugou.signatureGetter} ${keyword}"
        )
        val md5 = signatureProcess.inputStream.bufferedReader().readLine()

        signatureProcess.destroy()
        md5?.let {
            return it
        }

        throw IllegalStateException("生成酷狗音乐signature失败")
    }

    /**
     * 搜索所有音乐，返回包含搜索结果的列表(包含音乐Hash和专辑Hash)
     */
    suspend fun getMusicLists(keyword: String): MutableList<KugouSearchResualt.Data.Song> {
        if (!enabled) { return mutableListOf() }

        val md5 = getMd5(keyword)
        val url = "https://complexsearch.kugou.com/v2/search/song?callback=callback123&keyword=${
            withContext(Dispatchers.IO) {
                URLEncoder.encode(keyword, "UTF-8")
            }
        }" + "&page=1&pagesize=30&bitrate=0&isfuzzy=0&tag=&inputtype=0&platform=WebFilter&userid=0&clientver=2000&iscorrection=1" +
             "&privilege_filter=0&token=&srcappid=2919&clienttime=1645335325735&mid=1645335325735&uuid=1645335325735&dfid=-" +
             "&signature=" + md5

        val searchResultRaw = httpGetString(url).run { substring(12, length - 2) }

        val kugouSearchResualt = Gson().fromJson(searchResultRaw, KugouSearchResualt::class.java)
        return kugouSearchResualt.data.lists
    }

    /**
     * 通过音乐Hash和专辑Hash，获取音乐的url + 封面url等信息
     */
    suspend fun getMusic(sing: KugouSearchResualt.Data.Song): MusicResult {
        val url = "https://wwwapi.kugou.com/yy/index.php?r=play/getdata&hash=${sing.FileHash}&mid=s&album_id=${sing.AlbumID}"// + resualt.data.lists[0].FileHash
        val musicResultRaw = httpGetString(url)
        val kugouSing = Gson().fromJson(musicResultRaw, KugouSing::class.java)

        with(kugouSing.data) {
            return MusicResult(play_url, img, "${sing.SongName}${sing.Suffix}", sing.SingerName)
        }
    }

    data class KugouSearchResualt(val data: Data) {
        data class Data(val lists: MutableList<Song>) {
            data class Song(
                val SingerName: String,
                val FileHash: String,
                val SongName: String,
                val Suffix: String,
                val AlbumID: String,
            )
        }
    }

    data class KugouSing(val data: Data) {
        data class Data(
            val img: String,
            val play_url: String,
        )
    }
}