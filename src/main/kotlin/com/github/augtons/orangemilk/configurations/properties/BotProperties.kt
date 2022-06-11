package com.github.augtons.orangemilk.configurations.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * ```ymal
 * # ====机器人账户配置====
 * bot:
 *   # QQ号、密码
 *   qq: 这里替换成机器人的QQ号
 *   password: 这里替换成密码
 *
 *   # 机器人主人的账号(列表)，若仅有一个则删去一个
 *   # master的作用是当机器人启动之后，为主人发一条消息
 *   # 并且用于测试的监听器只为主人绑定（当然你可以手动修改逻辑）
 *   masters:
 *     - 主人1的QQ号
 *     - 主人2的QQ号
 * ```
 */
@Component
@ConfigurationProperties("bot")
class BotProperties {
    var qq: Long = 0L
    var password: String = ""

    var masters: List<Long> = emptyList()
}