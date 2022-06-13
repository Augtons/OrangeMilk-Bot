package com.github.augtons.orangemilk.game.idiomSolitaire

import com.github.augtons.orangemilk.game.core.GameFactory
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
        onFinish: (() -> Unit)?
    ): IdiomSolitaireGame? {
        return idiomUtil.enabled.then {
            IdiomSolitaireGame(eventChannel, (context as GroupMessageEvent), idiomUtil, onFinish)
        }
    }

}