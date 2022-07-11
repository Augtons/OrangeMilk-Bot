package com.github.augtons.orangemilk.framework.command.mc

import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.SingleMessage
import net.mamoe.mirai.message.data.content
import kotlin.reflect.KClass

/**
 * MC风格指令解析器，用于将消息链[MessageChain]解析出指令和参数
 * 支持Kotlin解构语法糖
 * ```
 * val (cmds, args) = parseMcCommand(messageChain)
 * ```
 * @see mcCommand
 * @see McCommandBuilder
 */
fun parseMcCommand(messageChain: MessageChain): Pair<List<String>, List<SingleMessage>> {
    val argAndCmd = mutableListOf<SingleMessage>().apply {
        messageChain.forEach {
            when(it) {
                is PlainText -> {
                    // 空格、点、句号、全角空格、中文逗号、英文逗号
                    addAll(it.content.split("\n", " ", ".", "。", "　", "，", ",").map { PlainText(it) })
                }
                else -> add(it)
            }
        }
    }

    val commands = argAndCmd.filter { it is PlainText && it.content.trim().startsWith("/") }
    val args = argAndCmd.toMutableList().apply {
        removeAt(0)
        removeIf { it in commands || it is PlainText && it.content.isBlank()}
    }

    return commands.map { it.content } to args
}

/**
 * 内联指令调用，并通过event中的消息解析参数
 */
suspend operator fun <E: MessageEvent> McCommand<E>.invoke(event: E) {
    val cmd = this.copy()
    val (_, args) = parseMcCommand(event.message)

    cmd.onCall(cmd.apply {
        context = event
        argtable = args
    })
}

/**
 * 内联指令调用，通过自定义MessageChain解析消息参数
 */
suspend operator fun <E: MessageEvent> McCommand<E>.invoke(event: E, message: MessageChain) {
    val cmd = this.copy()
    val (_, args) = parseMcCommand(message)

    cmd.onCall(cmd.apply {
        context = event
        argtable = args
    })
}

/**
 * MC风格指令类
 *
 * @see mcCommand
 */
class McCommand<E : MessageEvent>(
    val type: List<KClass<out E>>,
    val name: String = "",
    val prefixs: List<String> = emptyList(),
    //var
    var argtable: List<Any> = emptyList(),
    val filters: MutableList<(E) -> Boolean?> = mutableListOf(),
    val help: String = "",
    val needArgs: Boolean = false,
    //var
    var context: E?,
    val onCall: suspend (McCommand<E>) -> Unit
) {

    override fun equals(other: Any?): Boolean {
        return (other as? McCommand<*>)?.let { name == other.name } ?: false
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    fun copy(): McCommand<E> =
        McCommand(type, name, prefixs, argtable, filters, help, needArgs, context, onCall)
}