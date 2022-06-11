package com.github.augtons.orangemilk.listeners.commands

import com.github.augtons.orangemilk.command.mc.McCmd
import com.github.augtons.orangemilk.command.mc.McCommand
import com.github.augtons.orangemilk.command.mc.mcCommand
import com.github.augtons.orangemilk.command.registerCommand
import com.github.augtons.orangemilk.media.DiuProvider
import com.github.augtons.orangemilk.runtime.BotCommandSwitch
import com.github.augtons.orangemilk.utils.orNull
import kotlinx.coroutines.*
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

    val coroutineScope = CoroutineScope(Dispatchers.IO)

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
            executeDiu()
        }
    }

    suspend fun McCommand<GroupMessageEvent>.executeDiu() {
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

    @Deprecated(
        message = "通过异步下载来实现丢，但是经过测试发现接口传回的图片异常。或许是因为其他无法抵抗并发",
        replaceWith = ReplaceWith("executeDiu()")
    )
    suspend fun McCommand<GroupMessageEvent>.executeDiuAsync() {
        // 解析出qq号，存入列表
        val qqs = buildList {
            argtable.forEach {
                when (it) {
                    is At -> add(it.target.toString())
                    is PlainText -> add(it.content)
                }
            }
        }
        // 异步下载全部所需图片
        val diuResources = qqs.associateWith {
            coroutineScope.async {
                withTimeoutOrNull(8000) {
                    diuProvider.diu(it)?.let { file ->
                        file to file.toExternalResource()
                    }
                }
            }
        }
        // 发送
        // fileAndRes 是一个diu图片的文件及其ExternalResource对象
        diuResources.forEach { (qq, fileAndRes) ->
            try {
                val (file, res) = fileAndRes.await().orNull()

                with(context!!.subject) {
                    if (res != null) {
                        val imageRes = uploadImage(res)
                        sendMessage(imageRes)
                    } else {
                        sendMessage(PlainText("\"${qq}\"无法被丢"))
                    }
                }
                res?.close()
                file?.delete()
            }catch (_: Exception) {

            }
        }
    }
}