package com.github.augtons.orangemilk.game.core

import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MessageEvent

typealias GroupGameEntity = GameEntity<GroupMessageEvent>

class GameEntity<E: MessageEvent>(
    val name: String = "",
    val prefixs: List<String> = emptyList(),
    val filters: MutableList<(E) -> Boolean?> = mutableListOf(),
    val help: String = "",
    val factory: GameFactory<E>
)