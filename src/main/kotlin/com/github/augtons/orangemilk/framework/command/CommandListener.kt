package com.github.augtons.orangemilk.framework.command

import com.github.augtons.orangemilk.framework.command.mc.McCommand
import com.github.augtons.orangemilk.framework.command.mc.parseMcCommand
import com.github.augtons.orangemilk.framework.runtime.CommandManager
import com.github.augtons.orangemilk.utils.then
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.events.*
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class CommandListener(
    val bot: Bot,
    val commandManager: CommandManager
) {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    @PostConstruct
    fun init() {
        registerCommand(this, commandManager)

        // MC 风格指令
        listenToMcCommand()
    }

    /**
     * 监听MC风格指令
     */
    private fun listenToMcCommand() {
        // 1、群消息 GroupMessageEvent
        bot.eventChannel.filterIsInstance<GroupMessageEvent>()
            .subscribeAlways<GroupMessageEvent> event@{
                val (cmds, args) = parseMcCommand(message)

                val commandToCall = mutableListOf<McCommand<MessageEvent>>()

                cmds.forEach { cmd ->
                    commandManager.getMcCommand<GroupMessageEvent>(cmd, group.id)?.run {
                        // 满足过滤器则证明找到了这个命令
                        filters.all { it(this@event) ?: false }.then {
                            commandToCall += copy().apply {
                                context = this@event
                                if (needArgs) {
                                    argtable = args
                                }
                            }
                        }
                    }
                }

                commandToCall.forEach {
                    coroutineScope.launch {
                        try {
                            it.onCall(it)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }

        // 2、好友消息 FriendMessageEvent
        bot.eventChannel.filterIsInstance<FriendMessageEvent>()
            .subscribeAlways<FriendMessageEvent> event@{
                val (cmds, args) = parseMcCommand(message)

                val commandToCall = mutableListOf<McCommand<MessageEvent>>()

                cmds.forEach { cmd ->
                    commandManager.getMcCommand<FriendMessageEvent>(cmd, sender.id)?.run {
                        // 满足过滤器则证明找到了这个命令
                        filters.all { it(this@event) ?: false }.then {
                            commandToCall += copy().apply {
                                context = this@event
                                if (needArgs) {
                                    argtable = args
                                }
                            }
                        }
                    }
                }

                commandToCall.forEach {
                    coroutineScope.launch {
                        try {
                            it.onCall(it)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }

        // 3、群临时会话 GroupTempMessageEvent
        bot.eventChannel.filterIsInstance<GroupTempMessageEvent>()
            .subscribeAlways<GroupTempMessageEvent> event@{
                val (cmds, args) = parseMcCommand(message)

                val commandToCall = mutableListOf<McCommand<MessageEvent>>()

                cmds.forEach { cmd ->
                    commandManager.getMcCommand<GroupTempMessageEvent>(cmd, group.id)?.run {
                        // 满足过滤器则证明找到了这个命令
                        filters.all { it(this@event) ?: false }.then {
                            commandToCall += copy().apply {
                                context = this@event
                                if (needArgs) {
                                    argtable = args
                                }
                            }
                        }
                    }
                }

                commandToCall.forEach {
                    coroutineScope.launch {
                        try {
                            it.onCall(it)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }

        // 4、陌生人消息 StrangerMessageEvent
        bot.eventChannel.filterIsInstance<StrangerMessageEvent>()
            .subscribeAlways<StrangerMessageEvent> event@{
                val (cmds, args) = parseMcCommand(message)

                val commandToCall = mutableListOf<McCommand<MessageEvent>>()

                cmds.forEach { cmd ->
                    commandManager.getMcCommand<StrangerMessageEvent>(cmd, sender.id)?.run {
                        // 满足过滤器则证明找到了这个命令
                        filters.all { it(this@event) ?: false }.then {
                            commandToCall += copy().apply {
                                context = this@event
                                if (needArgs) {
                                    argtable = args
                                }
                            }
                        }
                    }
                }

                commandToCall.forEach {
                    coroutineScope.launch {
                        try {
                            it.onCall(it)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
    }
}