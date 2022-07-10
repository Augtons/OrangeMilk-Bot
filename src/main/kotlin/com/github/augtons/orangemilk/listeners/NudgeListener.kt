package com.github.augtons.orangemilk.listeners

import com.github.augtons.orangemilk.configurations.properties.BotProperties
import com.github.augtons.orangemilk.utils.TimeLimitedTask
import com.github.augtons.orangemilk.utils.nowMillis
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.NudgeEvent
import net.mamoe.mirai.message.data.*
import org.springframework.stereotype.Service

/**
 * ## 本监听器实现了机器人被戳的时候的响应
 * ### 功能：
 *  - 若机器人在群里被戳 -> 回戳并回复“@戳它的人  我在！”
 *  - 若机器人在私聊被戳 -> 回戳并回复“我在！”
 * ### 细节：
 *  - 机器人无法相应200ms(0.2秒)内被同一个人戳两次
 *  - 机器人相应之后接下来的60秒内只会戳别人20次（防止两个Bot递归）
 */
@Service
class NudgeListener(
    bot: Bot,
    botProperties: BotProperties,
) {
    // 上次戳Bot的人
    var lastNudger: Long = 0
    // 上次戳Bot的时间
    var lastTime: Long = System.currentTimeMillis()

    // 时间限制器
    val timeLimitedTask = TimeLimitedTask(20, 60_000)


    init {
        val nudgeChannel = bot.eventChannel
            .filterIsInstance<NudgeEvent>()
            .filter {
                (it.target.id == bot.id) &&
                (it.from.id != bot.id) &&
                (it.from.id != lastNudger || (nowMillis() - lastTime >= 200))
            }


        nudgeChannel.subscribeAlways<NudgeEvent> {
            when(subject) {
                is Group -> subject.sendMessage(At(it.from) + "  我在！" + Face(faces.random()))
                else -> subject.sendMessage(PlainText("我在！") + Face(faces.random()))
            }

            timeLimitedTask.happenSuspend {
                from.nudge().sendTo(subject)
            }

            lastNudger = from.id
            lastTime = System.currentTimeMillis()

        }

        bot.eventChannel.subscribeAlways<GroupMessageEvent> {
            message.forEach {
                if (it is FlashImage) {
                    val msg = buildMessageChain {
                        +PlainText("接收到闪照，在群${group.name}\n")
                        +PlainText("发送人：${senderName}\n")
                        +Image(it.image.imageId)
                    }

                    botProperties.masters.forEach {
                        bot.getFriend(it)?.sendMessage(msg)
                    }
                }
            }
        }
    }
}