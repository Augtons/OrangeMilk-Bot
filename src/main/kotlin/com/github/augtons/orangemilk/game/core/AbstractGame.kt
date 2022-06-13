package com.github.augtons.orangemilk.game.core

import net.mamoe.mirai.event.Listener
import net.mamoe.mirai.event.events.MessageEvent

abstract class AbstractGame(
    var onFinish: (() -> Unit)? = null
) {
    abstract val name: String
    abstract val listeners: MutableList<Listener<MessageEvent>>

    abstract fun start(argtable: List<Any>)

    open fun finish() {
        listeners.forEach {
            it.complete()
        }
        onFinish?.invoke()
    }
}