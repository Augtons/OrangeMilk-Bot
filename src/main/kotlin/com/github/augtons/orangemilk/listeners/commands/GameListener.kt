package com.github.augtons.orangemilk.listeners.commands

import com.github.augtons.orangemilk.command.mc.McCmd
import com.github.augtons.orangemilk.command.mc.mcCommand
import com.github.augtons.orangemilk.command.registerCommand
import com.github.augtons.orangemilk.game.core.AbstractGroupGame
import com.github.augtons.orangemilk.runtime.BotCommandSwitch
import com.github.augtons.orangemilk.runtime.RunningGroupGames
import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.PlainText
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class GameListener(
    val bot: Bot,
    val botCommandSwitch: BotCommandSwitch,
    runningGroupGames: RunningGroupGames,
) {
    @PostConstruct
    fun init() {
        registerCommand(this, botCommandSwitch)
    }

    @McCmd
    val createGame = mcCommand<GroupMessageEvent> {
        name = "create_game"
        prefix = listOf("/game", "/游戏")
        needArgs()

        onCall {
            val joinedName = argtable.filterIsInstance<PlainText>().joinToString("") { it.content.trim() }
            val gameEntity = buildList {
                    argtable.filterIsInstance<PlainText>().forEach {
                        addAll(it.content.trim().split("\n", " ", ".", "。", "　", "，", ","))
                    }
                }.firstOrNull {it in runningGroupGames.gameEntities }
                ?.let { name -> runningGroupGames.filterGameEntities(name, context!!) }
                ?: runningGroupGames.filterGameEntities(joinedName, context!!).apply {
                    context!!.subject.sendMessage("小警告: 请避免将游戏名分开，会影响游戏初始化参数传递")
                }

            val groupID = context!!.group.id
            val eventChannel = bot.eventChannel
                .filterIsInstance<GroupMessageEvent>()
                .filter {it.group.id == groupID}

            try {
                val game = gameEntity?.factory?.getGame(eventChannel, context!!) as AbstractGroupGame // 这一步表明gameEntity不为空
                game.onFinish = {
                    runningGroupGames.removeGame(groupID, game)
                }

                if(runningGroupGames.addGame(context!!.group.id, game)) {
                    context!!.subject.sendMessage("游戏开始：${game.name}\n\n规则: \n  ${gameEntity.help}")
                    game.start(argtable)
                } else {
                    context!!.subject.sendMessage("本群正在进行${game.name}游戏")
                }
            }catch (_: Exception) {
                context!!.subject.sendMessage("未找到群游戏：$joinedName")
            }
        }
    }
}