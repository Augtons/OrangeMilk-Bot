package com.github.augtons.orangemilk.controllers

import com.github.augtons.orangemilk.listeners.commands.GameListener
import com.github.augtons.orangemilk.listeners.commands.HelloAndHelps
import com.github.augtons.orangemilk.utils.logger
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class HelpUpdaterController(
    val helloAndHelps: HelloAndHelps,
    val gameListener: GameListener
) {
    val logger = logger<HelpUpdaterController>()

    @GetMapping("/help/reload")
    @ResponseBody
    fun reloadHelp(
        @RequestParam(required = true, defaultValue = "main") name: String
    ): String {

        return buildString {
            when (name) {
                "games" -> {
                    gameListener.loadHelp()

                    appendLine("已重加载 游戏列表 帮助<br/><br/>")
                    appendLine(gameListener.gameHelps.lineSequence().joinToString("<br/>"))
                    logger.info("已重加载 游戏列表 帮助")
                }
                // 默认
                else -> {
                    helloAndHelps.loadHelp()

                    appendLine("已重加载 main 帮助<br/><br/>")
                    appendLine(helloAndHelps.helpText.lineSequence().joinToString("<br/>"))
                    logger.info("已重加载 main 帮助")
                }
            }
        }

    }
}