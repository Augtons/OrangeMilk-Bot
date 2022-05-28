package com.github.augtons.orangemilk.listeners.commands

import com.github.augtons.orangemilk.command.mc.McCmd
import com.github.augtons.orangemilk.command.mc.mcCommand
import com.github.augtons.orangemilk.command.mc.mcCommand2
import com.github.augtons.orangemilk.command.mc.mcCommand4
import com.github.augtons.orangemilk.command.registerCommand
import com.github.augtons.orangemilk.media.music.KugouMusicSearcher
import com.github.augtons.orangemilk.media.music.NetEaseMusicSearcher
import com.github.augtons.orangemilk.media.sing.SingProvider
import com.github.augtons.orangemilk.runtime.BotCommandSwitch
import com.github.augtons.orangemilk.utils.logger
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.MusicKind
import net.mamoe.mirai.message.data.MusicShare
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class SingMusic(
    val botCommandSwitch: BotCommandSwitch,
    val singProvider: SingProvider,
    val kugouMusicSearcher: KugouMusicSearcher,
    val netEaseMusicSearcher: NetEaseMusicSearcher,
) {
    val logger = logger<SingMusic>()

    @PostConstruct
    fun init() {
        registerCommand(this, botCommandSwitch)
    }

    @McCmd
    val sing = mcCommand2<GroupMessageEvent, FriendMessageEvent> {
        name = "sing"
        prefix = listOf("/sing")

        onCall {
            singProvider.getSing()?.toExternalResource()?.use {
                val context = context
                when(context) {
                    is GroupEvent -> context.group.uploadAudio(it)
                    is FriendMessageEvent -> context.sender.uploadAudio(it)
                    else -> null
                }

            }?.let {
                context!!.subject.sendMessage(it)
            }
        }
    }

    @McCmd
    val kgMusic = mcCommand4<GroupMessageEvent, FriendMessageEvent, GroupTempMessageEvent, StrangerMessageEvent> {
        name = "kugou music"
        prefix = listOf("/music", "/kgmus", "/kugou", "/kgmusic", "/kg", "/酷狗")

        needArgs = true

        onCall {
            val keyWord = argtable.filterIsInstance<PlainText>().joinToString(" ")
            logger.debug("酷狗音乐搜索:" + keyWord)

            context!!.subject.sendMessage("正在为您搜索酷狗歌曲")
            try {
                kugouMusicSearcher.search(keyWord)?.let { music ->
                    context!!.subject.sendMessage(
                        MusicShare(
                            MusicKind.KugouMusic, music.musicName, music.singerName, music.playURL,
                            music.imgURL, music.playURL
                        )
                    )
                }
            }catch (_: Exception) {
                context!!.subject.sendMessage("酷狗点歌：\n未找到“$keyWord”\n换一个搜索词或改用其他平台搜索吧")
            }
        }
    }

    @McCmd
    val netEaseMusic = mcCommand(
        FriendMessageEvent::class,
        GroupMessageEvent::class,
        GroupTempMessageEvent::class,
        StrangerMessageEvent::class
    ) {
        name = "netease music"
        prefix = listOf("/netease", "/wyy", "/wangyi", "/wymusic", "/网易云")

        needArgs = true

        onCall {
            val keyWord = argtable.filterIsInstance<PlainText>().joinToString(" ").run {
            // 接口有bug，会屏蔽周杰伦，需要搜索“周杰lun”解决
                replace("周杰伦", "周杰lun")
            }

            context!!.subject.sendMessage("正在为您搜索网易云歌曲")

            try {
                netEaseMusicSearcher.searchSuspend(keyWord)?.let { music ->
                    context!!.subject.sendMessage(
                        MusicShare(
                            MusicKind.NeteaseCloudMusic, music.musicName, music.singerName, music.playURL,
                            music.imgURL, music.playURL
                        )
                    )
                }
            }catch (_e: Exception) {
                context!!.subject.sendMessage("网易云点歌：\n未找到“$keyWord”\n换一个搜索词或改用其他平台搜索吧")
            }
        }
    }
}