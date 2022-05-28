package com.github.augtons.orangemilk.configurations

import com.github.augtons.orangemilk.configurations.properties.BotProperties
import com.github.augtons.orangemilk.utils.logger
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.Bot
import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.network.LoginFailedException
import net.mamoe.mirai.utils.BotConfiguration.MiraiProtocol
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.annotation.PostConstruct
import kotlin.system.exitProcess

@Configuration
class BotBeans(
    val botProperties: BotProperties
) {
    val logger = logger<BotBeans>()

    @PostConstruct
    fun init() {
        with(botProperties) {
            if (qq == 0L || password.isBlank()) {
                throw IllegalStateException("未指定QQ号或密码")
            }
        }
    }

    @Bean
    fun bot(): Bot {
        val bot = BotFactory.newBot(botProperties.qq, botProperties.password) {
            fileBasedDeviceInfo()
            protocol = MiraiProtocol.IPAD
        }
        try {
            runBlocking {
                bot.login()
                botProperties.masters.forEach { qqcode ->
                    bot.getFriend(qqcode)?.sendMessage("登录成功:\n" +
                            LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))
                    )
                }
            }
        }catch (e: LoginFailedException) {
            logger.error("机器人登录失败")
            exitProcess(1)
        }

        return bot
    }
}