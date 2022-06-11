package com.github.augtons.orangemilk.controllers

import com.github.augtons.orangemilk.media.sing.SingProvider
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class SingController(
    val singProvider: SingProvider
) {

    @GetMapping("/media/sing/appreciations")
    @ResponseBody
    fun appreciations() = buildString {

        val unAppreciatedSings = singProvider.run { sings - appreciationMap.keys }
        val unUsedAppreciations = singProvider.run { appreciationMap.keys - sings }
        appendLine("【是否注入全部文案】：${unUsedAppreciations.isEmpty()}<br/>")
        appendLine("【是否全部歌曲已注入文案】：${singProvider.run { 
            sings.all { it in appreciationMap.keys }
        }}<br/>")
        appendLine("<br/>")
        if (unAppreciatedSings.isNotEmpty()) {
            appendLine("【未注入文案的歌曲】：<br/>")
            unAppreciatedSings.forEach {
                appendLine("\t${it.absolutePath}<br/>")
            }
        }
        appendLine("<br/>")
        if (unUsedAppreciations.isNotEmpty()) {
            appendLine("【未使用的文案】：<br/>")
            unUsedAppreciations.forEach { file ->
                appendLine(singProvider.appreciationMap[file]?.let {
                    "[${file.absolutePath}]: ${it}<br/>"
                } ?: "")
            }
        }

        appendLine("<br/>")
        appendLine("【全部文案】：<br/>")

        singProvider.sings.forEach { file ->
            appendLine("【${file.name}】 -> \n${singProvider.appreciationMap[file]}\n" + "<br/>")
        }
    }
}