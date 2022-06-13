package com.github.augtons.orangemilk.game.idiomSolitaire

import com.github.augtons.orangemilk.configurations.properties.GameProperties
import com.github.augtons.orangemilk.utils.logger
import org.springframework.stereotype.Component
import java.io.File
import java.nio.charset.Charset
import javax.annotation.PostConstruct

@Component
class IdiomUtil(
    val gameProperties: GameProperties
) {
    val logger = logger<IdiomUtil>()

    lateinit var allIdioms: List<Pair<String, List<String>>>
    lateinit var allIdiomsMap: Map<String, List<String>>
    lateinit var dictionary: Map<String, MutableSet<String>>

    val enabled: Boolean by lazy {
        val repo = gameProperties.idiom.repo
        repo.isNotBlank() && File(repo).exists()
    }

    @PostConstruct
    fun init() {
        if (!enabled) {
            logger.warn("成语接龙 —— 未设置成语库或成语库不存在")
            return
        }

        val charset = try {
            Charset.forName(gameProperties.idiom.repoCharset)
        } catch (_: Exception) {
            logger.warn("""成语接龙 —— 成语词库Charset设置错误："${gameProperties.idiom.repoCharset}"不存在""")
            return
        }

        val idiomsRepository = File(gameProperties.idiom.repo)
        allIdioms = idiomsRepository
            .readLines(charset)
            .map { line ->
                val (idiom, pinyin) = line.split("|")
                idiom to pinyin.split(" ", "，")
            }

        allIdiomsMap = allIdioms.associate { it }

        dictionary = buildMap {
            allIdioms.forEach { (idiom, pinyinList) ->
                val firstPinyin = pinyinList.first().removeShengdiao()

                if (this[firstPinyin] == null) {
                    this[firstPinyin] = mutableSetOf()
                }
                this[firstPinyin]!!.add(idiom)
            }
        }

        logger.info("成语工具初始化成功，已注入${allIdioms.size}个成语")
    }

    fun isValidNext(pre: String, newer: String): Boolean {
        if(allIdiomsMap[pre] == null || allIdiomsMap[newer] == null) {
            return false
        }

        return getLastWordPinyin(pre, false) == getFirstWordPinyin(newer, false)
    }

    fun getLastWordPinyin(idiom: String, shengdiao: Boolean = true): String? {
        val py = allIdiomsMap[idiom]?.last()
        return (if (shengdiao) py else py?.removeShengdiao())?.trim()
    }

    fun getFirstWordPinyin(idiom: String, shengdiao: Boolean = true): String? {
        val py = allIdiomsMap[idiom]?.first()
        return (if(shengdiao) py else py?.removeShengdiao())?.trim()
    }

    /**
     * 随机获取一个成语，返回成语和最后一个字的拼音
     * 支持Kotlin解构语法糖
     * ```kotlin
     * val (idiom, pinyin) = randomIdiom()
     * ```
     */
    fun randomIdiom() = allIdioms.random()
        .run { first to second.last() }

    fun randomNext(pre: String) = dictionary[getLastWordPinyin(pre, false)]?.random()

    fun isValidIdiom(idiom: String) = idiom in allIdiomsMap
}



fun String.removeShengdiao() = this
    .replace(Regex("[āáǎà]"), "a")
    .replace(Regex("[ōóǒò]"), "o")
    .replace(Regex("[ēéěè]"), "e")
    .replace(Regex("[īíǐì]"), "i")
    .replace(Regex("[ūúǔù]"), "u")
    .replace(Regex("[ǖǘǚǜü]"), "v")

private val shengdiaoRegex = listOf(
    Regex("[āáǎà]") to "a",
    Regex("[ōóǒò]") to "o",
    Regex("[ēéěè]") to "e",
    Regex("[īíǐì]") to "i",
    Regex("[ūúǔù]") to "u",
    Regex("[ǖǘǚǜü]") to "v"
)

fun String.removeShengdiao2(): String {
    return shengdiaoRegex // first: Regex, second: String
        .firstOrNull { it.first in this }
        ?.let { replace(it.first, it.second) }
        ?: this
}