package com.github.augtons.orangemilk.user.listeners

import com.github.augtons.orangemilk.properties.TempProperties
import com.github.augtons.orangemilk.utils.extendWith
import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.MemberJoinEvent
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import org.springframework.stereotype.Service
import java.io.File
import javax.annotation.PostConstruct

@Service
class IoTNaXin(
    val bot: Bot,
    final val tempProperties: TempProperties
) {
    val imgs: MutableList<File> = File(tempProperties.welcomeImgs).apply { mkdirs() }.walk()
        .filter { it.isFile && it.extendWith("png", "jpg", "jpeg", "gif") }
        .toMutableList()

    @PostConstruct
    fun init() {
        bot.eventChannel.filterIsInstance<MemberJoinEvent>()
//            .filter { it.group.id == 741713901L || it.group.id == 577350262L }
            .filter { it.group.id in tempProperties.iotGroups }
            .subscribeAlways<MemberJoinEvent> {
                this.group.sendMessage(
                    PlainText("欢迎 ") + At(user) + " 加入物联网科技协会大家庭！"
                )

                group.sendMessage(
                    imgs.random().uploadAsImage(group)
                )
            }

        bot.eventChannel.filterIsInstance<FriendMessageEvent>()
            .filter { it.sender.id == 3061322344 }
            .subscribeMessages {
                "测试欢迎" {
                    subject.sendMessage(
                        PlainText("欢迎 ") + At(sender) + " 加入物联网科技协会大家庭！"
                    )
                    subject.sendMessage(
                        imgs.random().uploadAsImage(subject)
                    )
                }
            }
    }
}