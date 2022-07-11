package com.github.augtons.orangemilk.user.game

import com.github.augtons.orangemilk.framework.game.GroupGame
import com.github.augtons.orangemilk.framework.game.groupGame
import com.github.augtons.orangemilk.framework.game.registerGroupGame
import com.github.augtons.orangemilk.user.game.idiomSolitaire.IdiomSolitaireGameFactory
import com.github.augtons.orangemilk.framework.runtime.GroupGameManager
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class GamesManifest(
    val groupGameManager: GroupGameManager,
    val idiomSolitaireGameFactory: IdiomSolitaireGameFactory
) {
    @PostConstruct
    fun init() {
        registerGroupGame(this, groupGameManager)
    }

    @GroupGame
    val idiomSolitaire = groupGame {
        name = "成语接龙"
        prefix = listOf("成语接龙")

        help = "回答一个成语，使得其第一个字与上一个成语的最后一个字读音相同(音调可不同)，首尾相接不断延伸。"

        factory(idiomSolitaireGameFactory)
    }
}