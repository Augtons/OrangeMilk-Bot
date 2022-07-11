package com.github.augtons.orangemilk.framework.game

import com.github.augtons.orangemilk.framework.runtime.GroupGameManager
import net.mamoe.mirai.event.events.GroupMessageEvent
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties

annotation class GroupGame

@DslMarker
annotation class GroupGameBuilderDSL

inline fun groupGame(block: GroupGameBuilder.() -> Unit): GroupGameEntity {
    return GroupGameBuilder().apply { block() }.build()
}

@Suppress("UNCHECKED_CAST")
fun registerGroupGame(context: Any, groupGameManager: GroupGameManager) {
    val groupGames = context.javaClass.kotlin.memberProperties.filter { it.hasAnnotation<GroupGame>() }

    groupGames.forEach {
        groupGameManager.registerGame(it.get(context) as GroupGameEntity)
    }
}

class GroupGameBuilder {
    @GroupGameBuilderDSL
    var name: String = ""

    @GroupGameBuilderDSL
    var prefix: List<String> = emptyList()

    @GroupGameBuilderDSL
    val filters = mutableListOf<(GroupMessageEvent) -> Boolean?>()

    @GroupGameBuilderDSL
    var help: String = ""

    @GroupGameBuilderDSL
    fun filter (condition: (GroupMessageEvent) -> Boolean?) {
        filters += condition
    }

    @GroupGameBuilderDSL
    var factory: GameFactory<GroupMessageEvent>? = null

    @GroupGameBuilderDSL
    fun factory(factory: GameFactory<GroupMessageEvent>) {
        this.factory = factory
    }

    fun build(): GroupGameEntity {
        factory ?: error("未设置游戏工厂实例(factory字段)")

        return GroupGameEntity(name, prefix, filters, help, factory!!)
    }
}