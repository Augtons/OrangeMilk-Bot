package com.github.augtons.orangemilk.user.commands

import com.github.augtons.orangemilk.framework.command.mc.McCmd
import com.github.augtons.orangemilk.framework.command.mc.mcCommandAllEvent
import com.github.augtons.orangemilk.framework.command.registerCommand
import com.github.augtons.orangemilk.properties.BotAppProperties
import com.github.augtons.orangemilk.listeners.faces
import com.github.augtons.orangemilk.framework.runtime.CommandManager
import com.github.augtons.orangemilk.utils.logger
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.PlainText
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct
import kotlin.io.path.Path

@Service
class HelloAndHelps(
    val commandManager: CommandManager,
    val botAppProperties: BotAppProperties,
) {
    val logger = logger<HelloAndHelps>()

    lateinit var helpText: String

    @PostConstruct
    fun init() {
        registerCommand(this, commandManager)
        loadHelp()
    }

    fun loadHelp() {
        Path(botAppProperties.help, "main.help").toFile().apply {
            parentFile.mkdirs()
            helpText = try {
                if(exists()) {
                    readText()
                } else {
                    logger.warn("未找到Bot主帮助文件: $canonicalPath")
                    "暂未设置帮助文本"
                }
            }catch (_: Exception) {
                logger.warn("读取Bot主帮助文件失败: $canonicalPath")
                "暂未设置帮助文本"
            }
        }
    }


    @McCmd
    val help = mcCommandAllEvent {
        name = "help"
        prefix = listOf("/help", "/list")

        onCall {
            context!!.subject.sendMessage(helpText)
        }
    }

    @McCmd
    val hello = mcCommandAllEvent {
        name = "hello"
        prefix = listOf("/hello", "/hi")

        onCall {
            with(context!!) {
                when(subject) {
                    is Group -> subject.sendMessage(At(sender) + "  我在！" + Face(faces.random()))
                    else -> subject.sendMessage(PlainText("我在！") + Face(faces.random()))
                }
                sender.nudge().sendTo(subject)
            }
        }
    }
}