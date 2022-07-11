package com.github.augtons.orangemilk.framework.command.mc

import net.mamoe.mirai.event.events.*
import kotlin.reflect.KClass

@DslMarker
annotation class McCommandBuilderDsl

annotation class McCmd

/**
 *  用于构建一个MC风格指令的DSL
 *
 *  此类DSL函数共有5个，分别是
 *  - 带有实化类型参数的[mcCommand]、[mcCommand2]、[mcCommand3]、[mcCommand4]
 *  - [mcCommand]的重载。无实化类型参数，需要通过`vararg`参数传递所有MessageEvent类
 *
 * ### 示例
 * ```
 * // 注意尖括号里的东西，此函数mcCommand，对应的是传入一个MessageEvent
 * @McCmd
 * val kgMusic = mcCommand<GroupMessageEvent> {
 *     name = "kugou music"  // 指令名字
 *     prefix = listOf("/music", "/kgmus", "/kugou", "/kgmusic", "/kg", "/酷狗") //指令的触发方式
 *     help = "用于酷狗音乐搜歌"
 *     needArgs = true  // 是否需要参数

 *     onCall {  // 指令触发时的动作，在里边写上指令的实现。方法同mirai

 *     }
 * }
 * ```
 * @see mcCommand
 * @see mcCommand2
 * @see mcCommand3
 * @see mcCommand4
 */
@McCommandBuilderDsl
inline fun <reified E : MessageEvent> mcCommand(block: McCommandBuilder<E>.() -> Unit): McCommand<E> {
    return McCommandBuilder<E>().apply { block(); type = listOf(E::class) }.build()
}

/**
 *  用于构建一个MC风格指令的DSL
 *
 *  此类DSL函数共有5个，分别是
 *  - 带有实化类型参数的[mcCommand]、[mcCommand2]、[mcCommand3]、[mcCommand4]
 *  - [mcCommand]的重载。无实化类型参数，需要通过`vararg`参数传递所有MessageEvent类
 *
 * ### 示例
 * ```
 * // 此函数为无实化类型参数的mcCommand，需要通过vararg参数传入MessageEvent，个数不限
 * @McCmd
 * val kgMusic = mcCommand(GroupMessageEvent::class, FriendMessageEvent::class, ...) {
 *     name = "kugou music"  // 指令名字
 *     prefix = listOf("/music", "/kgmus", "/kugou", "/kgmusic", "/kg", "/酷狗") //指令的触发方式
 *     help = "用于酷狗音乐搜歌"
 *     needArgs = true  // 是否需要参数

 *     onCall {  // 指令触发时的动作，在里边写上指令的实现。方法同mirai

 *     }
 * }
 * ```
 * @see mcCommand
 * @see mcCommand2
 * @see mcCommand3
 * @see mcCommand4
 */
@McCommandBuilderDsl
inline fun mcCommand(
    vararg event: KClass<out MessageEvent>, block: McCommandBuilder<MessageEvent>.() -> Unit
): McCommand<MessageEvent> {
    return McCommandBuilder<MessageEvent>().apply { block(); type = event.toList() }.build()
}

/**
 *  用于构建一个MC风格指令的DSL
 *
 *  此类DSL函数共有5个，分别是
 *  - 带有实化类型参数的[mcCommand]、[mcCommand2]、[mcCommand3]、[mcCommand4]
 *  - [mcCommand]的重载。无实化类型参数，需要通过`vararg`参数传递所有MessageEvent类
 *
 * ### 示例
 * ```
 * // 注意尖括号里的东西，此函数mcCommand2，对应的是传入两个MessageEvent
 * @McCmd
 * val kgMusic = mcCommand2<GroupMessageEvent, FriendMessageEvent> {
 *     name = "kugou music"  // 指令名字
 *     prefix = listOf("/music", "/kgmus", "/kugou", "/kgmusic", "/kg", "/酷狗") //指令的触发方式
 *     help = "用于酷狗音乐搜歌"
 *     needArgs = true  // 是否需要参数

 *     onCall {  // 指令触发时的动作，在里边写上指令的实现。方法同mirai

 *     }
 * }
 * ```
 * @see mcCommand
 * @see mcCommand2
 * @see mcCommand3
 * @see mcCommand4
 */
@McCommandBuilderDsl
inline fun <reified E1: MessageEvent, reified E2 : MessageEvent> mcCommand2(
    block: McCommandBuilder<MessageEvent>.() -> Unit
): McCommand<MessageEvent> {
    return McCommandBuilder<MessageEvent>().apply { block(); type = listOf(E1::class, E2::class) }.build()
}

/**
 *  用于构建一个MC风格指令的DSL
 *
 *  此类DSL函数共有5个，分别是
 *  - 带有实化类型参数的[mcCommand]、[mcCommand2]、[mcCommand3]、[mcCommand4]
 *  - [mcCommand]的重载。无实化类型参数，需要通过`vararg`参数传递所有MessageEvent类
 *
 * ### 示例
 * ```
 * // 注意尖括号里的东西，此函数mcCommand3，对应的是传入三个MessageEvent
 * @McCmd
 * val kgMusic = mcCommand3<GroupMessageEvent, FriendMessageEvent, GroupTempMessageEvent> {
 *     name = "kugou music"  // 指令名字
 *     prefix = listOf("/music", "/kgmus", "/kugou", "/kgmusic", "/kg", "/酷狗") //指令的触发方式
 *     help = "用于酷狗音乐搜歌"
 *     needArgs = true  // 是否需要参数

 *     onCall {  // 指令触发时的动作，在里边写上指令的实现。方法同mirai

 *     }
 * }
 * ```
 * @see mcCommand
 * @see mcCommand2
 * @see mcCommand3
 * @see mcCommand4
 */
@McCommandBuilderDsl
inline fun <reified E1: MessageEvent, reified E2: MessageEvent, reified E3: MessageEvent> mcCommand3(
    block: McCommandBuilder<MessageEvent>.() -> Unit
): McCommand<MessageEvent> {
    return McCommandBuilder<MessageEvent>().apply { block(); type = listOf(E1::class, E2::class, E3::class) }.build()
}

/**
 *  用于构建一个MC风格指令的DSL
 *
 *  此类DSL函数共有5个，分别是
 *  - 带有实化类型参数的[mcCommand]、[mcCommand2]、[mcCommand3]、[mcCommand4]
 *  - [mcCommand]的重载。无实化类型参数，需要通过`vararg`参数传递所有MessageEvent类
 *
 * ### 示例
 * ```
 * // 注意尖括号里的东西，此函数mcCommand4，对应的是传入四个MessageEvent
 * @McCmd
 * val kgMusic = mcCommand2<GroupMessageEvent, FriendMessageEvent，GroupTempMessageEvent, StrangerMessageEvent> {
 *     name = "kugou music"  // 指令名字
 *     prefix = listOf("/music", "/kgmus", "/kugou", "/kgmusic", "/kg", "/酷狗") //指令的触发方式
 *     help = "用于酷狗音乐搜歌"
 *     needArgs = true  // 是否需要参数

 *     onCall {  // 指令触发时的动作，在里边写上指令的实现。方法同mirai

 *     }
 * }
 * ```
 * @see mcCommand
 * @see mcCommand2
 * @see mcCommand3
 * @see mcCommand4
 */
@McCommandBuilderDsl
inline fun <reified E1: MessageEvent, reified E2: MessageEvent, reified E3: MessageEvent, reified E4: MessageEvent> mcCommand4(
    block: McCommandBuilder<MessageEvent>.() -> Unit
): McCommand<MessageEvent> {
    return McCommandBuilder<MessageEvent>().apply { block(); type = listOf(E1::class, E2::class, E3::class, E4::class) }.build()
}

/**
 *  用于构建一个MC风格指令的DSL
 *
 *  此类DSL函数共有5个，分别是
 *  - 带有实化类型参数的[mcCommand]、[mcCommand2]、[mcCommand3]、[mcCommand4]
 *  - [mcCommand]的重载。无实化类型参数，需要通过`vararg`参数传递所有MessageEvent类
 *
 * ### 示例
 * ```
 * // 此函数为代替``mcCommand4<GroupMessageEvent, FriendMessageEvent, GroupTempMessageEvent, StrangerMessageEvent>``的便捷函数
 * @McCmd
 * val kgMusic = mcCommandAllEvent {
 *     name = "kugou music"  // 指令名字
 *     prefix = listOf("/music", "/kgmus", "/kugou", "/kgmusic", "/kg", "/酷狗") //指令的触发方式
 *     help = "用于酷狗音乐搜歌"
 *     needArgs = true  // 是否需要参数

 *     onCall {  // 指令触发时的动作，在里边写上指令的实现。方法同mirai

 *     }
 * }
 * ```
 * @see mcCommand
 * @see mcCommand2
 * @see mcCommand3
 * @see mcCommand4
 */
@McCommandBuilderDsl
inline fun mcCommandAllEvent(block: McCommandBuilder<MessageEvent>.() -> Unit): McCommand<MessageEvent> {
    return McCommandBuilder<MessageEvent>().apply { block(); type = listOf<KClass<out MessageEvent>>(
        GroupMessageEvent::class, FriendMessageEvent::class, GroupTempMessageEvent::class, StrangerMessageEvent::class
    ) }.build()
}

@McCommandBuilderDsl
class McCommandBuilder<E : MessageEvent> {

    @McCommandBuilderDsl
    var type: List<KClass<out E>> = emptyList()

    @McCommandBuilderDsl
    var name: String = ""

    @McCommandBuilderDsl
    var prefix: List<String> = emptyList()

    @McCommandBuilderDsl
    var help: String = ""

    @McCommandBuilderDsl
    var needArgs: Boolean = false

    val filters = mutableListOf<(E) -> Boolean?>()
    var onCall: (suspend (McCommand<E>) -> Unit)? = null

    @McCommandBuilderDsl
    fun filter (condition: (E) -> Boolean?) {
        filters += condition
    }

    @McCommandBuilderDsl
    @Suppress("NOTHING_TO_INLINE")
    inline fun needArgs(bool: Boolean = true) {
        this.needArgs = bool
    }

    @McCommandBuilderDsl
    fun onCall (function: suspend McCommand<E>.() -> Unit) {
        onCall = function
    }

    fun build(): McCommand<E> {
        onCall ?: error("未实现指令(未添加onCall字段)")

        return McCommand(
            type, name, prefix, emptyList(), filters, help, needArgs, null, onCall!!
        )
    }
}