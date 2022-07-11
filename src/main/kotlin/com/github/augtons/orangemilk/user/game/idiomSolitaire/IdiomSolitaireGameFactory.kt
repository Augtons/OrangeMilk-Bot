package com.github.augtons.orangemilk.user.game.idiomSolitaire

import com.github.augtons.orangemilk.framework.game.GameFactory
import com.github.augtons.orangemilk.utils.then
import net.mamoe.mirai.event.EventChannel
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MessageEvent
import org.springframework.stereotype.Component

@Component
class IdiomSolitaireGameFactory(
    val idiomUtil: IdiomUtil,
): GameFactory<GroupMessageEvent>() {

    override fun getGame(
        eventChannel: EventChannel<GroupMessageEvent>,
        context: MessageEvent,
    ): IdiomSolitaireGame? {
        return idiomUtil.enabled.then {
            // IdiomSolitaire is a group game. So cast context to GroupMessageEvent anyway!
            IdiomSolitaireGame(eventChannel, (context as GroupMessageEvent), idiomUtil)
        }
    }

}