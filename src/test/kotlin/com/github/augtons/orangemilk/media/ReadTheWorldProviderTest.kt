package com.github.augtons.orangemilk.media

import com.github.augtons.orangemilk.configurations.properties.BotAppProperties
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class ReadTheWorldProviderTest {

    @Autowired
    lateinit var readTheWorldProvider: ReadTheWorldProvider

    @Autowired
    lateinit var botAppProperties: BotAppProperties

    @Test
    fun `has got cache path`() {
        println(botAppProperties.cacheDir)
    }

    @Test
    fun getImage() {
        runBlocking {
            readTheWorldProvider.getImage()
        }
    }
}