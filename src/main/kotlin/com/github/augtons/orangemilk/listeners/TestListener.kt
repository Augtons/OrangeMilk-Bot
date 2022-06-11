package com.github.augtons.orangemilk.listeners

import com.github.augtons.orangemilk.configurations.properties.BotProperties
import com.github.augtons.orangemilk.media.ReadTheWorldProvider
import com.github.augtons.orangemilk.media.WeatherProvider
import com.github.augtons.orangemilk.media.music.KugouMusicSearcher
import com.github.augtons.orangemilk.media.music.NetEaseMusicSearcher
import com.github.augtons.orangemilk.media.sing.SingProvider
import kotlinx.coroutines.withTimeout
import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.data.MusicKind
import net.mamoe.mirai.message.data.MusicShare
import net.mamoe.mirai.message.data.content
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import org.springframework.stereotype.Service
import java.io.File

@Service
class TestListener(
    bot: Bot,
    botProperties: BotProperties,
    kugouMusicSearcher: KugouMusicSearcher,
    netEaseMusicSearcher: NetEaseMusicSearcher,
    singProvider: SingProvider,
    readTheWorldProvider: ReadTheWorldProvider,
    weatherProvider: WeatherProvider
) {

    init {
        bot.eventChannel
            .filterIsInstance<GroupMessageEvent>()
            .filter { it.sender.id in botProperties.masters }
            .subscribeGroupMessages {

                startsWith("测试点歌：") {
                    val name = message.content.split("：")
                    val keyWord = if (name.size >= 2) {
                        name[1]
                    } else { null }

                    kugouMusicSearcher.search("$keyWord")?.let { music ->
                        subject.sendMessage(
                            MusicShare(
                                MusicKind.KugouMusic, music.musicName, music.singerName, music.playURL,
                                    music.imgURL, music.playURL
                            )
                        )
                    }
                }

                startsWith("测试网易云：") {
                    val name = message.content.split("：")
                    val keyWord = if (name.size >= 2) {
                        name[1]
                    } else { null }

                    subject.sendMessage("正在为您搜索网易云音乐歌曲")

                    netEaseMusicSearcher.searchSuspend("$keyWord")?.let { music ->
                        subject.sendMessage(
                            MusicShare(
                                MusicKind.NeteaseCloudMusic, music.musicName, music.singerName, music.playURL,
                                music.imgURL, music.playURL
                            )
                        )
                    }

                }

                "测试唱歌" {
                    var sing: File? = null
                    var appreciation: String? = null

                    for(i in 1..100) {
                        val (s, a) = singProvider.getSingWithAppreciate()
                        if(a != null) {
                            sing = s!!
                            appreciation = a
                            break
                        }
                    }

                    sing!!.toExternalResource().use {
                        group.uploadAudio(it)
                    }.let {
                        appreciation?.let { subject.sendMessage(it) }
                        subject.sendMessage(it)
                    }
                }

                "测试新闻" {
                    try {
                        val resource = withTimeout(8000) {
                            readTheWorldProvider.getImage()?.toExternalResource()
                        }
                        resource!!.use {
                            subject.sendMessage(subject.uploadImage(it))
                        }
                    }catch (_: Exception) {
                        subject.sendMessage("获取失败")
                    }
                }

                startsWith("测试天气：") {
                    val name = message.content.split("：")
                    val keyWord = if (name.size >= 2) {
                        name[1]
                    } else { null }

                    keyWord?.let {
                        weatherProvider.getWeather(it)?.also { weather ->
                            subject.sendMessage(weather)
                        } ?: subject.sendMessage("未找到关键词“${it}”的天气，可以尝试换一个关键词哦")
                    }
                }
            }
    }
}