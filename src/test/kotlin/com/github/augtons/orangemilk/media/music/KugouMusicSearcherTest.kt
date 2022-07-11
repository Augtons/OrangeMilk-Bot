package com.github.augtons.orangemilk.media.music

import com.github.augtons.orangemilk.properties.MusicProperties
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class KugouMusicSearcherTest {

    @Autowired
    lateinit var kugouMusicSearcher: KugouMusicSearcher

    @Autowired
    lateinit var musicProperties: MusicProperties

    @Test
    fun getMd5() {
        val md5 = kugouMusicSearcher.getMd5("迈兮")
        assert(md5 == "206B7655E2EE8E9D9A6639E68A142ADD")
    }

    @Test
    fun getMusicProperties() {
        assert(musicProperties.kugou.signatureGetter.isNotBlank())
    }

    @Test
    fun getFirstMusic() {
        runBlocking {
            kugouMusicSearcher.searchSuspend("迈兮").let {
                println(it)
            }
        }
    }
}