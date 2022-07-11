package com.github.augtons.orangemilk.media

import com.github.augtons.orangemilk.properties.SingProperties
import com.github.augtons.orangemilk.utils.extendWith
import com.github.augtons.orangemilk.utils.fromJSONString
import com.github.augtons.orangemilk.utils.logger
import com.github.augtons.orangemilk.utils.then
import org.springframework.stereotype.Service
import java.io.File
import javax.annotation.PostConstruct
import kotlin.io.path.Path

@Service
class SingProvider(
    val singProperties: SingProperties
) {
    private val logger = logger<SingProvider>()
    final var sings: Set<File> = setOf()
        private set
    final var appreciationMap = mapOf<File, String?>()
        private set

    private var playingSings = mutableSetOf<File>()

    @PostConstruct
    fun init() {
        val allSings = File(singProperties.path).apply { mkdirs() }
            .walk()
            .filter { it.isFile && it.extendWith("mp3", "amr") }
            .toList()

        allSings.isEmpty().then {
            logger.warn("""本地Sing目录"${singProperties.path}"不存在mp3或amr文件""")
        }

        sings = allSings.toSet()

        // 搜索后缀名为template的文件，解析出歌曲文案的模板。仅使用找到的第一个
        // 注意，搜索深度为1，即只搜索sing目录下的文件
        val appreciationTemplate = File(singProperties.path).walk()
            .maxDepth(1)
            .filter { it.isFile && it.extension == "template" }
            .firstOrNull()
            ?.readText()

        // 搜索后缀名为aps的文件，解析出歌曲文案
        // 搜索深度不限，即搜索sing目录下的所有文件
        val appreciationFiles = File(singProperties.path).walk()
            .filter { it.isFile && it.extendWith("aps", "json") }


        // 解析出歌曲文案
        val singsAppreciations = mutableSetOf<Pair<File, String?>>()
        appreciationFiles.forEach { file ->
            val path = file.parentFile.absolutePath // 设置当前目录为赏析文件的目录
            val manifest = file.readText().fromJSONString<LocalSingManifest>()

            manifest.sings.forEach { sing -> // sings是manifest文件json的字段，每个sing包含了关于歌曲的文件信息和文案
                sing.file.forEach { _file -> // _file 为歌曲信息的文件字段的列表项目
                    val singFile = Path(path, _file).toFile().canonicalFile
                    if(singFile.exists()) {
                        singsAppreciations.add(singFile to sing.generate(appreciationTemplate))
                    } else {
                        logger.warn("""赏析文件"${file.absolutePath}"中的歌曲"${sing.file}"不存在""")
                    }
                }
            }
        }

        // 将赏析文件中的歌曲文案添加到Map中，方便后期查找
        appreciationMap = buildMap {
            singsAppreciations.forEach { (sing, appreciation) ->
                if(sing in this.keys) {
                    logger.warn("发现重复赏析：${sing.absolutePath}")
                }
                this[sing] = appreciation
            }
        }

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

    fun getSingWithAppreciate(): Pair<File?, String?> {
        val sing = getSing() ?: return null to null
        return sing to appreciationMap[sing]
    }

    data class LocalSingManifest(
        val sings: MutableList<LocalSing>
    )

    data class LocalSing(
        val name: String,
        val file: List<String>,
        val tag: String,
        val sg: String,
        val appreciation: String,
    ) {
        fun generate(template: String?) = template?.format(tag, sg ,appreciation)
    }
}