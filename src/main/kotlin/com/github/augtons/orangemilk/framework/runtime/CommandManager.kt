package com.github.augtons.orangemilk.framework.runtime

import com.github.augtons.orangemilk.framework.command.mc.McCommand
import net.mamoe.mirai.event.events.MessageEvent
import org.springframework.stereotype.Service
import kotlin.reflect.KClass

typealias McCommandSet = MutableSet<CommandManager.McCommandSetEntity>

/**
 * Bot 指令实现存放的位置，和指令的开关
 */
@Service
class CommandManager {
    /**
     * 存放运行时全部MC风格命令的map
     * map的Key：MessageEvent的子类，如GroupMessageEvent表示群聊指令
     * map的value: 此类型的全部命令列表
     */
    var mcCommandMap: MutableMap<KClass<out MessageEvent>, MutableList<McCommandSetEntity>> = mutableMapOf()

    data class McCommandSetEntity(
        val command: McCommand<MessageEvent>, // 思考题：为什么这里不写McCommand<out MessageEvent>
                                              // 答案：McCommand<>的type字段的声明：val type: List<KClass<out E>>，已包含out的语义
        val disabledIDs: MutableSet<Long> // 禁止的ID，若命令为群命令，则此为已经关闭的群，否则为已经开启的群
    )

    fun addMcCommand(mcCommand: McCommand<MessageEvent>) {
        mcCommand.type.forEach { type ->
            // 若此类型的命令为空，则创建一个新可变列表
            if (mcCommandMap[type] == null) {
                mcCommandMap[type] = mutableListOf()
            }
            // 加入一个新命令实体
            mcCommandMap[type]!! += McCommandSetEntity(mcCommand, mutableSetOf())
        }
    }

    fun getMcCommand(
        prefix: String,
        id: Long? = null,
        type: KClass<out MessageEvent>? = null
    ): McCommand<MessageEvent>? {
        if (type != null) {
            return mcCommandMap[type]?.firstOrNull {
                prefix in it.command.prefixs && (id == null || id !in it.disabledIDs)
            }?.command
        } else {
            mcCommandMap.values.forEach { _list ->
                val commandSetEntity = _list.firstOrNull {
                    prefix in it.command.prefixs && (id == null || id !in it.disabledIDs)
                }
                return commandSetEntity?.command
            }
            return null
        }
    }

    final inline fun <reified E : MessageEvent> getMcCommand(
        prefix: String,
        id: Long? = null
    ) = getMcCommand(prefix, id, E::class)
}

@Deprecated("请使用getMcCommand")
operator fun McCommandSet.get(
    prefix: String
): CommandManager.McCommandSetEntity? {
    return firstOrNull { prefix in it.command.prefixs }
}

@Deprecated("请使用getMcCommand")
operator fun McCommandSet.get(
    prefix: String, groupID: Long
): CommandManager.McCommandSetEntity? {
    return firstOrNull { prefix in it.command.prefixs && groupID !in it.disabledIDs}
}
