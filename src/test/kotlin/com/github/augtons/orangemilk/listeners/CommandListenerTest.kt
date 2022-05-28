package com.github.augtons.orangemilk.listeners

import com.github.augtons.orangemilk.command.McCmd
import com.github.augtons.orangemilk.command.mcCommand
import com.github.augtons.orangemilk.command.registerCommand
import com.github.augtons.orangemilk.runtime.BotCommandSwitch
import net.mamoe.mirai.contact.MemberPermission
import net.mamoe.mirai.event.events.GroupMessageEvent
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class CommandListenerTest {

    @Autowired
    lateinit var botCommandSwitch: BotCommandSwitch

    @Test
    fun testBuilder() {
        registerCommand(this, botCommandSwitch)

        println(botCommandSwitch.mcCommandSet)
        println(botCommandSwitch.mcCommandSet)
    }

    @McCmd
    val cmd1 = mcCommand<GroupMessageEvent> {
        name = "sdsd"
        prefix = listOf("/hi", "/hello")

        filter { it.permission > MemberPermission.MEMBER }
        filter { it.group.id == 123456789L }

        onCall {
            println("hehehehe")
        }
    }
}