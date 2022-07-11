package com.github.augtons.orangemilk.media.music

import com.github.augtons.orangemilk.properties.MusicProperties
import com.github.augtons.orangemilk.utils.buildUri
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class NetEaseMusicSearcherTest {
    @Autowired
    lateinit var netEaseMusicSearcher: NetEaseMusicSearcher

    @Autowired
    lateinit var musicProperties: MusicProperties

    @Test
    fun `if app got api address from yml`() {
        netEaseMusicSearcher.musicProperties.netease.apiUrl.let {
            assertEquals("http://81.68.161.75:3000/", it)
        }
    }

    @Test
    fun `拼接 uri`() {
        buildUri(musicProperties.netease.apiUrl) {
            path("/login/status")
        }.let {
            println(it)
        }
    }

    @Test
    fun search() {
        println(netEaseMusicSearcher.search("锦零观星台"))
    }

    @Test
    fun searchSuspend() {
        runBlocking {
            netEaseMusicSearcher.searchSuspend("锦零观星台")
        }
    }

    @Test
    fun getMusicLists() {
        runBlocking {
            netEaseMusicSearcher.getMusicLists("锦零观星台").let {
                println(it)
            }
        }
    }

    @Test
    fun getMusic() {
        runBlocking {
            val l = netEaseMusicSearcher.getMusicLists("芒种音阙诗听")
            println(l.first())
            netEaseMusicSearcher.getMusic(l.first()).let {
                println(it)
            }
        }
    }

    @Test
    fun getCoverImg() {
        runBlocking {
            val l = netEaseMusicSearcher.getMusicLists("芒种音阙诗听")
            println(l.first())
            netEaseMusicSearcher.getCoverImg(l.first()).let {
                println(it)
            }
        }
    }

    @Test
    fun getLoginStatus() {
        runBlocking {
            println(netEaseMusicSearcher.getLoginStatus())
        }
    }

}