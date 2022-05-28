package com.github.augtons.orangemilk.listeners.commands

import com.github.augtons.orangemilk.command.mc.McCmd
import com.github.augtons.orangemilk.command.mc.mcCommand
import com.github.augtons.orangemilk.command.registerCommand
import com.github.augtons.orangemilk.media.DiuProvider
import com.github.augtons.orangemilk.media.ReadTheWorldProvider
import com.github.augtons.orangemilk.runtime.BotCommandSwitch
import kotlinx.coroutines.withTimeout
import net.mamoe.mirai.event.events.GroupMessageEvent
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

    @PostConstruct
    fun init() {
        registerCommand(this, botCommandSwitch)
    }

    @McCmd
    val diu = mcCommand<GroupMessageEvent> {
        name = "diu"
        prefix = listOf("/diu", "/丢")

        needArgs()

        onCall {
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
                    val imgFile = withTimeout(8000) { diuProvider.diu(qq) }

                    val imgResource = imgFile?.toExternalResource()

                    with(context!!.subject) {
                        if (imgResource != null) {
                            val imageRes = uploadImage(imgResource)
                            sendMessage(imageRes)
                        } else {
                            sendMessage(PlainText("\"${qq}\"无法被丢"))
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
}