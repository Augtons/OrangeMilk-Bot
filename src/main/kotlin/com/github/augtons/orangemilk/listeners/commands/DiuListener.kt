package com.github.augtons.orangemilk.listeners.commands

import com.github.augtons.orangemilk.command.mc.McCmd
import com.github.augtons.orangemilk.command.mc.McCommand
import com.github.augtons.orangemilk.command.mc.mcCommand4
import com.github.augtons.orangemilk.command.mc.mcCommandAllEvent
import com.github.augtons.orangemilk.command.registerCommand
import com.github.augtons.orangemilk.media.DiuProvider
import com.github.augtons.orangemilk.runtime.BotCommandSwitch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withTimeout
import net.mamoe.mirai.event.events.*
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
    val diu = mcCommand4<GroupMessageEvent, FriendMessageEvent, GroupTempMessageEvent, StrangerMessageEvent> {
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
        name = "bao"
        prefix = listOf("/bao", "/huge", "/抱")
        needArgs()

        onCall {
            executeDiu(DiuProvider.Mode.BAO)
        }
    }

    @McCmd
    val pound = mcCommandAllEvent {
        name = "pound"
        prefix = listOf("/chui", "/pound", "/锤")
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

            try {
                if(qq.isBlank()){
                    throw Exception("QQ号不能为空")
                }
                val imgFile = withTimeout(8000) { diuProvider.diu(qq, mode) }

                val imgResource = imgFile?.toExternalResource()

                with(context!!.subject) {
                    if (imgResource != null) {
                        val imageRes = uploadImage(imgResource)
                        sendMessage(imageRes)
                    } else {
                        sendMessage(PlainText("\"${qq}\"无法被${mode.label}"))
                    }
                }
                imgResource?.close()
                imgFile?.delete()
            }catch (_: Exception) {
//                    context!!.subject.sendMessage(PlainText("获取失败"))
            }
        }
    }
}