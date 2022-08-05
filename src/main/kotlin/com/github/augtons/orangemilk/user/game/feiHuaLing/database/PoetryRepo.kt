package com.github.augtons.orangemilk.user.game.feiHuaLing.database

import com.github.augtons.orangemilk.user.game.feiHuaLing.database.entities.PoetryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface PoetryRepo: JpaRepository<PoetryEntity, Int> {

    fun findBySubSentence(sentence: String): MutableList<PoetryEntity>

    @Query("select w.word from fhl_words w;", nativeQuery = true)
    fun getWords(): MutableList<String>
}