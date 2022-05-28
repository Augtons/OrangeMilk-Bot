package com.github.augtons.orangemilk.listeners.commands

import com.github.augtons.orangemilk.command.mc.McCmd
import com.github.augtons.orangemilk.command.mc.mcCommand
import com.github.augtons.orangemilk.command.mc.mcCommand4
import com.github.augtons.orangemilk.command.registerCommand
import com.github.augtons.orangemilk.media.ReadTheWorldProvider
import com.github.augtons.orangemilk.media.WeatherProvider
import com.github.augtons.orangemilk.runtime.BotCommandSwitch
import kotlinx.coroutines.withTimeout
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.GroupTempMessageEvent
import net.mamoe.mirai.event.events.StrangerMessageEvent
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class WeatherNews(
    val botCommandSwitch: BotCommandSwitch,
    val weatherProvider: WeatherProvider,
    val readTheWorldProvider: ReadTheWorldProvider,
) {

    @PostConstruct
    fun init() {
        registerCommand(this, botCommandSwitch)
    }

    @McCmd
    val weather = mcCommand4<GroupMessageEvent, FriendMessageEvent, GroupTempMessageEvent, StrangerMessageEvent> {
        name = "weather"
        prefix = listOf("/weather", "/tq", "/天气")

        needArgs = true

        onCall {
            val keyWord = argtable.filterIsInstance<PlainText>().joinToString("%20")

            try {
                val weather = weatherProvider.getWeather(keyWord)
                    ?: "未找到关键词“${keyWord}”的天气，可以尝试换一个关键词哦"
                context!!.subject.sendMessage(weather)
            }catch (_: Exception) {
                context!!.subject.sendMessage("请求失败")
            }
        }
    }

    @McCmd
    val news = mcCommand4<GroupMessageEvent, FriendMessageEvent, GroupTempMessageEvent, StrangerMessageEvent> {
        name = "news"
        prefix = listOf("/news", "/新闻")

        onCall {
            try {
                val resource = withTimeout(8000) {
                    readTheWorldProvider.getImage()?.toExternalResource()
                }
                resource!!.use {
                    context!!.run {
                        subject.sendMessage(subject.uploadImage(it))
                    }
                }
            }catch (_: Exception) {
                context!!.subject.sendMessage("获取失败")
            }
        }
    }
}