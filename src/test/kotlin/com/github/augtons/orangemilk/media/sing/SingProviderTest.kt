package com.github.augtons.orangemilk.media.sing

import com.github.augtons.orangemilk.media.SingProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class SingProviderTest {

    @Autowired
    lateinit var singProvider: SingProvider
}