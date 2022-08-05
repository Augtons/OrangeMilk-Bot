package com.github.augtons.orangemilk.user.game.idiomSolitaire

import com.github.augtons.orangemilk.framework.game.AbstractGroupGame
import com.github.augtons.orangemilk.user.game.Timer
import com.github.augtons.orangemilk.utils.logger
import com.github.augtons.orangemilk.utils.nowMillis
import kotlinx.coroutines.*
import net.mamoe.mirai.event.EventChannel
import net.mamoe.mirai.event.Listener
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildMessageChain
import java.util.LinkedList
import kotlin.math.min

class IdiomSolitaireGame(
    private val eventChannel: EventChannel<GroupMessageEvent>,
    private val context: GroupMessageEvent,
    private val idiomUtil: IdiomUtil,
) : AbstractGroupGame() {

    private val logger = logger<IdiomSolitaireGame>()

    override val name: String get() = "成语接龙"

    override val players: MutableSet<Long> get() = mutableSetOf()
    override val scores: MutableMap<Long, Double> = mutableMapOf()

    private val playNames: MutableMap<Long, String> = mutableMapOf()
    // 储存玩家上一次提示的时间
    private val playerHintRequestTimes: MutableMap<Long, Long> = mutableMapOf()

    private lateinit var argtable: List<Any>

    override val listeners: MutableList<Listener<MessageEvent>> = mutableListOf()

    private var lastIdiom: String = ""
    private var lastPinyin: String = ""

    private val recentIdioms: LinkedList<String> = LinkedList()

    private var maxRound = 50   // 最大回合数，默认50
    private var round = 1       // 当前回合数

    private val maxTime = 120  // 回合最大时间
    private val timeAfterHind = 59 // 提示之后，当前回合的时间变为
    private val hindCD = 90_000 // 用户提示技能的CD数，单位：毫秒

    private val timer = Timer(maxTime) { finish() }.apply {
        timeWachers[30] = { context.subject.sendMessage("还剩30秒了") }
        timeWachers[60] = {
            context.subject.sendMessage("还剩一分钟了")
            context.subject.sendMessage(
                """
                |输入『排名』查看实时排名
                |可以输入『提示』/『跳过』哦
                |
                |我们正在接：${lastIdiom}($lastPinyin)
                """.trimMargin()
            )
        }
    }

    infix fun String.next(newer: String) = idiomUtil.isValidNext(this, newer)

    override fun start(argtable: List<Any>) {
        this.argtable = argtable

        // 获取全部的数字参数
        val intArgs = buildList {
            argtable.filterIsInstance<PlainText>().forEach {
                addAll(it.content.trim().split("\n", " ", ".", "。", "　", "，", ","))
            }
        }.mapNotNull { it.toIntOrNull() }
        // 设置时间，最小1最大200，默认30
        intArgs.lastOrNull()?.let {
            maxRound = if(it <= 0) 30 else min(200, it)
        }
        // 初始化第一个成语
        val (idiom, lastpy) = idiomUtil.randomIdiomHasNext()
        lastIdiom = idiom
        lastPinyin = lastpy

        runBlocking {
            delay(400)
            context.subject.sendMessage("共${maxRound}回合(默认50)")
            context.subject.sendMessage("第一个成语: $lastIdiom($lastPinyin)")
        }

        timer.start()
        // 订阅消息，处理消息
        listeners += eventChannel.subscribeAlways {
            val msg = message.filterIsInstance<PlainText>().joinToString("") { it.content.trim() }
            if (msg.isBlank()) {
                return@subscribeAlways
            }
            if(idiomUtil.isValidIdiom(msg)) {
                if (lastIdiom next msg) { // 有人回答正确

                    // 加人、加分、记回合数
                    round ++
                    if(sender.id !in scores) {
                        playNames[sender.id] = senderName
                        join(sender.id)
                    }
                    addScore(sender.id, 1.0)

                    lastIdiom = msg
                    lastPinyin = idiomUtil.getLastWordPinyin(msg)!!

                    timer.time = maxTime

                    if(round <= maxRound) {
                        subject.sendMessage(
                            PlainText("恭喜") + At(sender) + "回答正确\n" +
                            """
                            |当前分数为${scores[sender.id]} (+1.0)
                            |
                            |第${round}/${maxRound}回合
                            |新成语：${lastIdiom}($lastPinyin)
                            """.trimMargin()
                        )
                    } else {
                        subject.sendMessage(
                            PlainText("恭喜") + At(sender) + "回答正确\n" +
                            "当前分数为${scores[sender.id]} (+1.0)"
                        )
                        finish()
                    }
                    // 判断玩家说的这个成语是不是可以接，如果不能接，换一个
                    if (idiomUtil.pinyinHasNext(lastPinyin).not()) {
                        subject.sendMessage(
                            buildString {
                                append("“${lastIdiom}($lastPinyin)”这个成语好像接不了哎，我来给你们换一个吧\n\n")
                                val (newIdiom, newLastpy) = idiomUtil.randomIdiomHasNext()
                                lastIdiom = newIdiom
                                lastPinyin = newLastpy
                                append("新成语：${lastIdiom}($lastPinyin)")
                            }
                        )
                    }
                    // 判断是否已经发生了循环刷分（连续3个答对的成语都是同一个）
                    if (updateRecentIdioms(lastIdiom)) { // 套娃了
                        subject.sendMessage(
                            buildString {
                                append("你们怎么开始套娃了，我来给你们换一个吧\n\n")
                                lastIdiom = idiomUtil.randomNextHasNextAndExcept(lastIdiom)!!
                                lastPinyin = idiomUtil.getLastWordPinyin(lastIdiom)!!
                                append("新成语：${lastIdiom}($lastPinyin)")
                            }
                        )
                        updateRecentIdioms(lastIdiom)
                    }
                } else { // 有人说了个成语，但是不能接上
                    subject.sendMessage(
                        At(sender) + "\n" + """
                        |“$msg(${idiomUtil.getFirstWordPinyin(msg)})”接不上哦
                        |
                        |我们正在接：${lastIdiom}($lastPinyin)
                        """.trimMargin())
                }
            }

            when(msg) {
                "退出", "结束", "结束游戏", "退出游戏" -> {
                    if (timer.time > 45) {
                        subject.sendMessage("距离上次有人回答还很近哦。再等${timer.time - 45}秒如果没人回答再结束吧")
                    } else {
                        finish()
                    }
                }
                "提示", "跳过" -> {
                    if (sender.id !in scores && scores.isNotEmpty()) {
                        subject.sendMessage("你还没答对过题哦，先参与进来再“提示/跳过”吧")
                    } else {
                        // 第一步：检查他上一次提示的时间
                        val time = playerHintRequestTimes[sender.id]
                        // 1.null表示还没有提示过；2.当前的时间(ms)与上次提示时间的差值小于CD
                        if (time != null && (nowMillis() - time) < hindCD) {
                            // 这个人的提示技能CD还未冷却
                            subject.sendMessage(
                                buildMessageChain {
                                    +"你不是刚提示了嘛"
                                    +Face(Face.撇嘴)
                                    +At(sender)
                                    +"\n你再过${(hindCD-(nowMillis()-time))/1000}秒才能提示哦~"
                                }
                            )
                            return@subscribeAlways
                        }
                        // 第二步：若上文没有返回，则发动提示
                        timer.time = timeAfterHind // 时间重启
                        // 机器人接一个成语，并且获取一个能接的
                        lastIdiom = idiomUtil.randomNextHasNext(lastIdiom)!!
                        lastPinyin = idiomUtil.getLastWordPinyin(lastIdiom)!!

                        playerHintRequestTimes[sender.id] = nowMillis()

                        subject.sendMessage(
                        """
                        |那就浅浅地跳过喽
                        |
                        |新成语：${lastIdiom}($lastPinyin)
                        """.trimMargin()
                        )
                        subject.sendMessage(PlainText("我再等一分钟哦~") + Face(Face.期待))
                    }
                }
                "分数", "成绩", "当前分数", "状态", "排行", "当前排行", "排名" -> {
                    subject.sendMessage("""
                        |当前为第${round}回合
                        |${getRank() ?: "暂无排行榜"}
                        |
                        |我们正在接：${lastIdiom}($lastPinyin)
                        """.trimMargin()
                    )
                }
            }
        }
    }

    /**
     * 更新最近成语
     * @return true 当最近几次(典型值：3次)成语均相同时，认定为套娃发生
     *
     * false 同上，均不同。认定为套娃未发生
     */
    private fun updateRecentIdioms(idiom: String): Boolean {
        recentIdioms.addLast(idiom)
        if (recentIdioms.size > 3) {
            recentIdioms.removeFirst()
        }
        return if(recentIdioms.size >= 3) {
            //通过set去重之后长度为1，因此认为最近三次均为同一个成语，即套娃
            recentIdioms.toSet().size == 1
        } else false
    }


    private fun getRank(): String? {
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
                "输入“/game 成语接龙”重新开始"
            } else {
                "排行: \n${getRank()}\n输入“/game 成语接龙”重新开始"
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