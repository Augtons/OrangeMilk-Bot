package com.github.augtons.orangemilk.listeners.commands

import com.github.augtons.orangemilk.command.mc.McCmd
import com.github.augtons.orangemilk.command.mc.McCommand
import com.github.augtons.orangemilk.command.mc.mcCommandAllEvent
import com.github.augtons.orangemilk.command.registerCommand
import com.github.augtons.orangemilk.media.DiuProvider
import com.github.augtons.orangemilk.runtime.BotCommandSwitch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withTimeout
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class DiuListener(
    val botCommandSwitch: BotCommandSwitch,
    val diuProvider: DiuProvider,
) {

    val coroutineScope = CoroutineScope(Dispatchers.IO)

    @PostConstruct
    fun init() {
        registerCommand(this, botCommandSwitch)
    }

    @McCmd
    val diu = mcCommandAllEvent {
        name = "diu"
        prefix = listOf("/diu", "/丢")

        needArgs()

        onCall {
            executeDiu()
        }
    }

    @McCmd
    val pai = mcCommandAllEvent {
        name = "pai"
        prefix = listOf("/pai", "/拍")
        needArgs()

        onCall {
            executeDiu(DiuProvider.Mode.PAI)
        }
    }

    @McCmd
    val grab = mcCommandAllEvent {
        name = "grab"
        prefix = listOf("/zhua", "/grab", "/抓")
        needArgs()

        onCall {
            executeDiu(DiuProvider.Mode.GRAB)
        }
    }

    @McCmd
    val bao = mcCommandAllEvent {
        name = "bao"   //指令名
        prefix = listOf("/bao", "/huge", "/抱")  // 触发语句
        needArgs()  // 是否接收参数（会传到onCall代码段里）

        // 示例：过滤器这么写
        // filter { it.sender.id == 123456789L }   // 过滤发送者
        // filter { it is GroupMessageEvent && it.group.id == 3452367L } // 过滤群消息的群号

        onCall {    //触发动作
            executeDiu(DiuProvider.Mode.BAO)
        }
    }

    @McCmd
    val pound = mcCommandAllEvent {
        name = "pound"
        prefix = listOf("/chui", "/pound", "/锤", "/捶")
        needArgs()

        onCall {
            executeDiu(DiuProvider.Mode.POUND)
        }
    }

    suspend fun McCommand<MessageEvent>.executeDiu(mode: DiuProvider.Mode = DiuProvider.Mode.DIU) {
        argtable.forEach {
            val qq = when (it) {
                is At -> it.target.toString()
                is PlainText -> it.content
                else -> ""
            }

            if(qq.isBlank()) { return@forEach }

            val imgFile = withTimeout(8000) { diuProvider.diu(qq, mode) }

            try {
                imgFile?.toExternalResource().use { resource ->
                    with(context!!.subject) {
                        if (resource != null) {
                            val imageRes = uploadImage(resource)
                            sendMessage(imageRes)
                        } else {
                            sendMessage(PlainText("\"${qq}\"无法被${mode.label}"))
                        }
                    }
                }
            } finally {
                imgFile?.delete()
            }
        }
    }
}