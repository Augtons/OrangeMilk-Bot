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
        |★注意：空格！空格！空格！
        |
        |一、含参指令
        |  1.酷狗点歌：/kugou 歌名
        |　　　别名：/music、/kg、/kgmus、/kgmusic、/酷狗
        |  2.网易云点歌: /wyy 歌名
        |  　　别名：/wymusic、/netease、/wangyi、/网易云
        |  3.天气: /weather 地名
        |  　　别名：/tq、/天气
        |  4.[仅群聊]丢群友: /diu @人或QQ号
        |  　　别名：/丢
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