package com.github.augtons.orangemilk.user.game.feiHuaLing

import com.github.augtons.orangemilk.properties.GameProperties
import com.github.augtons.orangemilk.user.game.feiHuaLing.database.PoetryRepo
import com.github.augtons.orangemilk.user.game.feiHuaLing.database.entities.PoetryEntity
import com.github.augtons.orangemilk.utils.logger
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class FeiHuaLingUtil(
    val poetryRepo: PoetryRepo,
    val gameProperties: GameProperties
) {
    private val logger = logger<FeiHuaLingUtil>()
    val words: List<String> by lazy { poetryRepo.getWords() }

    val enabled: Boolean by lazy {
        gameProperties.feiHuaLing.enable && words.isNotEmpty()
    }

    @PostConstruct
    fun init() {
        if (!enabled) {
            logger.warn("飞花令游戏未启动或配置无效")
            return
        }

        logger.info("飞花令游戏初始化成功，注入了${words.size}个题目")
    }

    fun isSentenceInRepo(sentence: String): Boolean {
        return poetryRepo.findBySubSentence(sentence).isNotEmpty()
    }

    fun sentenceInRepo(sentence: String): List<PoetryEntity> {
        return poetryRepo.findBySubSentence(sentence)
    }
}