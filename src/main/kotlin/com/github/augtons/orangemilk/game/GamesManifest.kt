package com.github.augtons.orangemilk.game

import com.github.augtons.orangemilk.game.core.GroupGame
import com.github.augtons.orangemilk.game.core.groupGame
import com.github.augtons.orangemilk.game.core.registerGroupGame
import com.github.augtons.orangemilk.game.idiomSolitaire.IdiomSolitaireGameFactory
import com.github.augtons.orangemilk.runtime.RunningGroupGames
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class GamesManifest(
    val runningGroupGames: RunningGroupGames,
    val idiomSolitaireGameFactory: IdiomSolitaireGameFactory
) {
    @PostConstruct
    fun init() {
        registerGroupGame(this, runningGroupGames)
    }

    @GroupGame
    val idiomSolitaire = groupGame {
        name = "成语接龙"
        prefix = listOf("成语接龙")

        help = "回答一个成语，使得其第一个字与上一个成语的最后一个字读音相同(音调可不同)，首尾相接不断延伸。"

        factory(idiomSolitaireGameFactory)
    }
}