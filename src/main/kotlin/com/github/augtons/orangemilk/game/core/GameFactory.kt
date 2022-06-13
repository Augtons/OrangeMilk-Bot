package com.github.augtons.orangemilk.game.core

import net.mamoe.mirai.event.EventChannel
import net.mamoe.mirai.event.events.MessageEvent

abstract class GameFactory<E: MessageEvent> {

    abstract fun getGame(
        eventChannel: EventChannel<E>,
        context: MessageEvent,
        onFinish: (() -> Unit)? = null
    ): AbstractGame?
}