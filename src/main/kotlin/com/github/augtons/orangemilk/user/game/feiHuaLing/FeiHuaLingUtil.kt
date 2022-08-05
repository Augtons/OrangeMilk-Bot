package com.github.augtons.orangemilk.user.game.feiHuaLing

import com.github.augtons.orangemilk.user.game.feiHuaLing.database.PoetryRepo
import com.github.augtons.orangemilk.user.game.feiHuaLing.database.entities.PoetryEntity
import org.springframework.stereotype.Component

@Component
class FeiHuaLingUtil(
    val poetryRepo: PoetryRepo
) {
    val words: List<String> by lazy { poetryRepo.getWords() }

    fun isSentenceInRepo(sentence: String): Boolean {
        return poetryRepo.findBySubSentence(sentence).isNotEmpty()
    }

    fun sentenceInRepo(sentence: String): List<PoetryEntity> {
        return poetryRepo.findBySubSentence(sentence)
    }
}