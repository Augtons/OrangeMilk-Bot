package com.github.augtons.orangemilk.user.game

import com.github.augtons.orangemilk.framework.game.GroupGame
import com.github.augtons.orangemilk.framework.game.groupGame
import com.github.augtons.orangemilk.framework.game.registerGroupGame
import com.github.augtons.orangemilk.framework.runtime.GroupGameManager
import com.github.augtons.orangemilk.user.game.feiHuaLing.FeiHuaLingGameFactory
import com.github.augtons.orangemilk.user.game.idiomSolitaire.IdiomSolitaireGameFactory
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class GamesManifest(
    val groupGameManager: GroupGameManager,
    val idiomSolitaireGameFactory: IdiomSolitaireGameFactory,
    val feiHuaLingGameFactory: FeiHuaLingGameFactory,
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

    @GroupGame
    val feihualing = groupGame {
        name = "飞花令"
        prefix = listOf("飞花令")

        help = """
            |根据题目中提供的字，对出包含此字的诗句。
            |
            |范围：《唐诗三百首》、《全唐诗》、《全宋诗》、《全宋词》、《南唐二主词》、《诗经》、《楚辞》、《曹操诗集》、《花间集》、《纳兰性德诗集》、部分元曲""".trimMargin()
        factory(feiHuaLingGameFactory)
    }
}