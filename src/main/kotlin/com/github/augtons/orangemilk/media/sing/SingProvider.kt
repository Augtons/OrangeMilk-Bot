package com.github.augtons.orangemilk.media.sing

import com.github.augtons.orangemilk.configurations.properties.SingProperties
import com.github.augtons.orangemilk.utils.logger
import com.github.augtons.orangemilk.utils.then
import org.springframework.stereotype.Service
import java.io.File
import javax.annotation.PostConstruct

@Service
class SingProvider(
    val singProperties: SingProperties
) {
    private val logger = logger<SingProvider>()
    private var sings: Set<File> = setOf()

    private var playingSings = mutableSetOf<File>()

    @PostConstruct
    fun init() {
        val allSings = File(singProperties.path).apply { mkdirs() }.listFiles { file ->
            file.isFile && (file.extension.equals("mp3", true) || file.extension.equals("amr", true))
        }

        allSings?.isEmpty()?.then {
            logger.warn("""本地Sing目录"${singProperties.path}"不存在mp3或amr文件""")
        }

        sings = allSings?.toSet() ?: emptySet()
    }

    fun getSing(): File? {
        if (sings.isEmpty()) {
            logger.error("音乐列表为空")
            return null
        }

        if (playingSings.isEmpty()) {
            playingSings += sings
        }

        return playingSings.random().also { playingSings.remove(it) }
    }
}