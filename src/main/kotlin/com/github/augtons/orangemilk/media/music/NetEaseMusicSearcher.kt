package com.github.augtons.orangemilk.media.music

import com.github.augtons.orangemilk.configurations.properties.MusicProperties
import com.github.augtons.orangemilk.utils.buildUri
import com.github.augtons.orangemilk.utils.httpGetString
import com.github.augtons.orangemilk.utils.logger
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.*
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

/**
 * ## NetEaseMusicSearcher
 * ### 功能：
 *  - 用于网易云音乐搜歌
 *
 * ### 示例：
 * ```
 * val sing = netEaseMusicSearcher.search("锦零 观星台")
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
class NetEaseMusicSearcher(
    val musicProperties: MusicProperties
) : MusicSearcher() {

    private val logger = logger<NetEaseMusicSearcher>()
    // 是否可用，判断是否已指定网易云音乐API地址
    val enabled: Boolean by lazy { musicProperties.netease.apiUrl.isNotBlank() }

    @PostConstruct
    fun init() {
        if (!enabled) {
//            throw IllegalStateException("未指定网易云音乐API地址")
            logger.warn("未指定网易云音乐API地址, NetEaseMusicSearcher.enabled = $enabled")
        } else {
            logger.info("网易云搜歌已启用, NetEaseMusicSearcher.enabled = $enabled")
        }
    }

    /**
     * 搜索获取全部结果的第一首歌
     */
    override fun search(keyword: String): MusicResult? {
        return runBlocking(Dispatchers.IO) {
            searchSuspend(keyword)
        }
    }

    /**
     * 搜索获取全部结果的第一首歌，但是该函数是挂起版本
     */
    suspend fun searchSuspend(keyword: String): MusicResult? {
        if (!enabled) { return null }

        try {
            val sings = getMusicLists(keyword)

            for (sing in sings) {
                try {
                    return getMusic(sing)
                } catch (_e: Exception) {
                    continue
                }
            }
            throw IllegalStateException("网易云点歌：未找到“$keyword”")
        } catch (e: Exception) {
            logger.error(e.message)
            return null
        }
    }

    suspend fun getMusicLists(keyword: String): List<NetEaseSearchResult.NetEaseSong> {
        val url = buildUri(musicProperties.netease.apiUrl) {
            path("/search")
            queryParam("keywords", keyword)
        }
        val jsonString = httpGetString(url)
        val resultJson = JsonParser.parseString(jsonString).asJsonObject["result"]
        return Gson().fromJson(resultJson, NetEaseSearchResult::class.java).songs
    }

    suspend fun getMusic(sing: NetEaseSearchResult.NetEaseSong): MusicResult {
        val url = buildUri(musicProperties.netease.apiUrl) {
            path("/song/url")
            queryParam("id", sing.id)
        }
        val jsonString = httpGetString(url)
        val singResult = Gson().fromJson(jsonString, NetEaseSing::class.java).data.first()
        val imageUrl = getCoverImg(sing)

        return MusicResult(
            singResult.url,
            imageUrl,
            sing.name,
            sing.artists.map { it.name }.joinToString("、")
        )
    }

    suspend fun getCoverImg(sing: NetEaseSearchResult.NetEaseSong): String {
        val url = buildUri(musicProperties.netease.apiUrl) {
            path("/song/detail")
            queryParam("ids", sing.id)
        }
        val jsonString = httpGetString(url)
        return Gson().fromJson(jsonString, NetEaseSongDetails::class.java).songs.first().al.picUrl
    }

    suspend fun getLoginStatus(): Boolean {
        val url = buildUri(musicProperties.netease.apiUrl) {
            path("/login/status")
        }
        val jsonString = httpGetString(url)
        return JsonParser.parseString(jsonString).asJsonObject["data"].asJsonObject["account"] != null
    }

    data class NetEaseSearchResult(
        val songs: List<NetEaseSong>
    ) {
        data class NetEaseSong(
            val id: Long,
            val name: String,
            val artists: List<Artists>,
            val album: Album
        ) {
            data class Artists(
                val name: String
            )
            data class Album(
                val id: Long?
            )
        }
        /*参考数据
        {
				"id": 1927170510,
				"name": "观星台",
				"artists": [
					{
						"id": 12277185,
						"name": "锦零",
						"picUrl": null,
						"alias": [],
						"albumSize": 0,
						"picId": 0,
						"img1v1Url": "https://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg",
						"img1v1": 0,
						"trans": null
					}
				],
				"album": {
					"id": 141630830,
					"name": "观星台",
					"artist": {
						"id": 0,
						"name": "",
						"picUrl": null,
						"alias": [],
						"albumSize": 0,
						"picId": 0,
						"img1v1Url": "https://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg",
						"img1v1": 0,
						"trans": null
					},
					"publishTime": 1646928000000,
					"size": 2,
					"copyrightId": -1,
					"status": 0,
					"picId": 109951167135254930,
					"mark": 0
				},
				"duration": 176666,
				"copyrightId": 0,
				"status": 0,
				"alias": [],
				"rtype": 0,
				"ftype": 0,
				"mvid": 0,
				"fee": 8,
				"rUrl": null,
				"mark": 536879104
			},*/
    }

    @Deprecated("不知道这**接口为啥下午能用，现在就不行了")
    data class NetEaseAlbumDatail(
        val album: Album
    ) {
        data class Album(
            val coverUrl: String
        )
    }

    data class NetEaseSongDetails(
        val songs: List<SongDetail2>
    ) {
        data class SongDetail2(
            val al: Album
        ) {
            data class Album(
                val picUrl: String
            )
        }
    }

    data class NetEaseSing(
        val data: List<Data>
    ) {
        data class Data(
            val url: String
        )
    }
}