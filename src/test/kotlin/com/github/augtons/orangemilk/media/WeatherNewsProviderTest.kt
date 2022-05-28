package com.github.augtons.orangemilk.media

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class WeatherNewsProviderTest {

    @Autowired
    lateinit var weatherProvider: WeatherProvider

    @Test
    fun getWeather() {
        runBlocking {
            weatherProvider.getWeather("河北石家庄").let {
                println(it)
            }
        }
    }
}