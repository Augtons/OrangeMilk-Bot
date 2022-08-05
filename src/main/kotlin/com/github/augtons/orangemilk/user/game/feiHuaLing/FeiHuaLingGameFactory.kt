package com.github.augtons.orangemilk.user.game.feiHuaLing

import com.github.augtons.orangemilk.framework.game.GameFactory
import com.github.augtons.orangemilk.utils.then
import net.mamoe.mirai.event.EventChannel
import net.mamoe.mirai.event.events.GroupMessageEvent
import org.springframework.stereotype.Component

@Component
class FeiHuaLingGameFactory(
    private val feiHuaLingUtil: FeiHuaLingUtil,
): GameFactory<GroupMessageEvent>() {

    override fun getGame(
        eventChannel: EventChannel<GroupMessageEvent>,
        context: GroupMessageEvent
    ): FeiHuaLingGame? {
        return feiHuaLingUtil.enabled.then {
            FeiHuaLingGame(eventChannel, context, feiHuaLingUtil)
        }
    }
}