package com.github.augtons.orangemilk.game.core

import net.mamoe.mirai.event.Listener
import net.mamoe.mirai.event.events.MessageEvent

abstract class AbstractGame {
    abstract val name: String
    abstract val listeners: MutableList<Listener<MessageEvent>>

    // event Listeners
    var onFinish: (() -> Unit)? = null

    abstract fun start(argtable: List<Any>)

    open fun finish() {
        listeners.forEach {
            it.complete()
        }
        onFinish?.invoke()
    }
}