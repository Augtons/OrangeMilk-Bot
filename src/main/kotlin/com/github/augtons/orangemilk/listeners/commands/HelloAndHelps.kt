package com.github.augtons.orangemilk.listeners.commands

import com.github.augtons.orangemilk.command.mc.McCmd
import com.github.augtons.orangemilk.command.mc.mcCommand4
import com.github.augtons.orangemilk.command.registerCommand
import com.github.augtons.orangemilk.runtime.BotCommandSwitch
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.GroupTempMessageEvent
import net.mamoe.mirai.event.events.StrangerMessageEvent
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class HelloAndHelps(
    val botCommandSwitch: BotCommandSwitch
) {

    @PostConstruct
    fun init() {
        registerCommand(this, botCommandSwitch)
    }

    val helpText = """
        |注意：指令和参数，以及指令之间要加空格
        |
        |一、含参指令
        |  1.酷狗点歌: /music、/kgmus、/kugou、/kgmusic、/kg、/酷狗" + 歌名
        |  2.网易云点歌: /netease、/wyy、/wangyi、/wymusic、/网易云 + 歌名
        |  3.天气: /weather、/tq、/天气 + 全世界地名
        |  4.[仅群聊]丢群友: /diu、/丢 + @人 或 QQ号
        |  
        |二、无参指令
        |  1.帮助: /help、/list
        |  2.打招呼: /hi、/hello
        |  3.给你唱歌: /sing
        |  4.查新闻: /news、/新闻
    """.trimMargin()

    @McCmd
    val help = mcCommand4<GroupMessageEvent, FriendMessageEvent, GroupTempMessageEvent, StrangerMessageEvent> {
        name = "help"
        prefix = listOf("/help", "/list")

        onCall {
            context!!.subject.sendMessage(helpText)
        }
    }
}