package com.github.augtons.orangemilk.user.game.feiHuaLing

import com.github.augtons.orangemilk.framework.game.AbstractGroupGame
import com.github.augtons.orangemilk.user.game.Timer
import com.github.augtons.orangemilk.utils.logger
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.event.EventChannel
import net.mamoe.mirai.event.Listener
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildMessageChain

class FeiHuaLingGame(
    private val eventChannel: EventChannel<GroupMessageEvent>,
    private val context: GroupMessageEvent,
    private val feiHuaLingUtil: FeiHuaLingUtil
): AbstractGroupGame() {

    private val logger = logger<FeiHuaLingGame>()
    override val name: String = "飞花令"
    override val listeners: MutableList<Listener<MessageEvent>> = mutableListOf()

    override val players: MutableSet<Long> get() = mutableSetOf()
    override val scores: MutableMap<Long, Double> = mutableMapOf()
    private val playNames: MutableMap<Long, String> = mutableMapOf()

    private val gamingWord = feiHuaLingUtil.words.random()

    private val usedSentence = mutableSetOf<String>()

    private val maxTime = 120

    private val timer = Timer(maxTime) { finish() }.apply {
        timeWachers[30] = { context.subject.sendMessage("还剩30秒了") }
        timeWachers[60] = {
            context.subject.sendMessage("还剩一分钟了")
            context.subject.sendMessage(
                """
                |输入『排名』查看实时排名
                |
                |请回答含有“${gamingWord}”的诗句
                """.trimMargin()
            )
        }
    }

    override fun start(argtable: List<Any>) {
        runBlocking {
            context.subject.sendMessage("本局抽到的题目是: “${gamingWord}”")
        }

        timer.start()

        listeners += eventChannel.subscribeAlways {
            val msg = message.filterIsInstance<PlainText>().joinToString("") { it.content.trim() }
            if (msg.isBlank()) {
                return@subscribeAlways
            }
            if (msg in usedSentence) {
                subject.sendMessage(
                    At(sender) + "\n" + """
                        |“${msg}”这句有人回答过了哦
                        |
                        |换一句含有“${gamingWord}”的诗句吧
                        """.trimMargin())
                return@subscribeAlways
            }

            when(msg) {
                "退出", "结束", "结束游戏", "退出游戏" -> {
                    if (timer.time > 45) {
                        subject.sendMessage("距离上次有人回答还很近哦。再等${timer.time - 45}秒如果没人回答再结束吧")
                    } else {
                        finish()
                    }
                    return@subscribeAlways
                }

                "分数", "成绩", "当前分数", "状态", "排行", "当前排行", "排名" -> {
                    subject.sendMessage("""
                        |${getRank() ?: "暂无排行榜"}
                        |
                        |请回答含有“${gamingWord}”的诗句
                        """.trimMargin()
                    )
                    return@subscribeAlways
                }
            }

            val poetry = feiHuaLingUtil.sentenceInRepo(msg).firstOrNull() ?: return@subscribeAlways

            // 若能执行到下边，则消息内容在诗词库中
            if (gamingWord in msg) {
                // 回答正确
                if(sender.id !in scores) {
                    playNames[sender.id] = senderName
                    join(sender.id)
                }
                addScore(sender.id, 1.0)
                timer.time = maxTime

                usedSentence += msg

                subject.sendMessage(
                    PlainText("恭喜") + At(sender) + "回答正确\n" +
                            """
                            |当前分数为${scores[sender.id]} (+1.0)
                            |
                            |此句出自中：《${poetry.book}》
                            |　${poetry.sentence}
                            |　　——《${poetry.title}》(${poetry.author})
                            """.trimMargin()
                )
            } else {
                // 回答错误
                subject.sendMessage(
                    At(sender) + "\n" + """
                        |“${msg}”不可以哦
                        |
                        |请回答含有“${gamingWord}”的诗句哦
                        """.trimMargin())
            }

        }
    }

    fun getRank(): String? {
        return if(scores.isNotEmpty()) {
            scores.toList().sortedByDescending { it.second }.mapIndexed { index, (qq, score) ->
                "【${index + 1}】${playNames[qq]}\n    得分: $score"
            }.joinToString("\n")
        }
        else null
    }

    override fun finish() {
        super.finish()

        timer.coroutineScope.cancel()

        runBlocking {
            val rank = if (scores.isEmpty()) {
                "输入“/game 飞花令”重新开始"
            } else {
                "排行: \n${getRank()}\n输入“/game 飞花令”重新开始"
            }

            context.subject.sendMessage(
                if (scores.isEmpty()) { PlainText("游戏结束了！") } else {
                    buildMessageChain {
                        scores.keys.forEach { +At(it) }
                        +"\n\n游戏结束了！"
                    }
                }
            )
            context.subject.sendMessage(rank)
        }
    }
}