package com.github.augtons.orangemilk.framework.command

import com.github.augtons.orangemilk.framework.command.mc.McCmd
import com.github.augtons.orangemilk.framework.command.mc.McCommand
import com.github.augtons.orangemilk.framework.runtime.CommandManager
import net.mamoe.mirai.event.events.MessageEvent
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties


@Suppress("UNCHECKED_CAST")
fun registerCommand(context: Any, commandManager: CommandManager) {
    val mcCmds = (context.javaClass.kotlin).memberProperties.filter { it.hasAnnotation<McCmd>() }

    mcCmds.forEach {
        val command = it.get(context) as McCommand<MessageEvent> // 被@McCmd标记的熟悉
        commandManager.addMcCommand(command)
    }
}

