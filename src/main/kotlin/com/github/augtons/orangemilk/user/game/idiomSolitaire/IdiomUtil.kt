package com.github.augtons.orangemilk.user.game.idiomSolitaire

import com.github.augtons.orangemilk.properties.GameProperties
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

    /**
     * 随机获取一个可以接的成语
     */
    fun randomIdiomHasNext(): Pair<String, String> {
        for (i in 1..100) {
            val (cy, py) = randomIdiom()
            if (pinyinHasNext(py)) {
                return cy to py
            }
        }
        error("成语库异常，未获取到可以接的成语")
    }

    /**
     * 随机获取下一个成语
     */
    fun randomNext(pre: String) = dictionary[getLastWordPinyin(pre, false)]?.random()

    /**
     * 随机获取下一个成语，并且保证与原来的不同
     */
    fun randomNextExcept(pre: String): String? {
        for (i in 1..100) {
            val next = randomNext(pre) ?: return null
            if (next != pre) {
                return next
            }
        }
        error("成语库异常，未获取到不相同的成语")
    }

    /**
     * 随机获取下一个可以接的成语
     */
    fun randomNextHasNext(pre: String): String? {
        for (i in 1..100) {
            val next = randomNext(pre) ?: return null
            if (hasNext(next)) {
                return next
            }
        }
        error("成语库异常，未获取到可以接的成语")
    }

    /**
     * 随机获取下一个可以接的成语，并且保证与原来的不同
     */
    fun randomNextHasNextAndExcept(pre: String): String? {
        for (i in 1..100) {
            val next = randomNext(pre) ?: return null
            if (hasNext(next) && next != pre) {
                return next
            }
        }
        error("成语库异常，未获取不相同的且可以接的成语")
    }

    /**
     * 判断当前成语是否可以接
     */
    fun hasNext(pre: String) = getLastWordPinyin(pre, false) in dictionary

    /**
     * 判断当前是否存在这个拼音开头的成语（声调可以不同的）
     */
    fun pinyinHasNext(lastPinyin: String) = lastPinyin.removeShengdiao() in dictionary

    /**
     * 判断一个词是否为成语
     */
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