package com.github.augtons.orangemilk.framework.runtime

import com.github.augtons.orangemilk.framework.game.AbstractGroupGame
import com.github.augtons.orangemilk.framework.game.GroupGameEntity
import com.github.augtons.orangemilk.utils.then
import net.mamoe.mirai.event.events.GroupMessageEvent
import org.springframework.stereotype.Service

@Service
class GroupGameManager {

    val gameEntities: MutableMap<String, GroupGameEntity> = mutableMapOf()

    val runningGroupGames: MutableMap<Long, MutableSet<AbstractGroupGame>> = mutableMapOf()

    fun registerGame(groupGameEntity: GroupGameEntity) {
        groupGameEntity.prefixs.forEach { prefix ->
            gameEntities[prefix] = groupGameEntity
        }
    }

    /**
     * 过滤游戏注册实体
     * @param prefix 用于触发游戏
     * @param messageEvent 用于匹配过滤器
     */
    fun filterGameEntities(prefix: String, messageEvent: GroupMessageEvent): GroupGameEntity? {
        val game = gameEntities[prefix]
        return (game?.filters?.all { it(messageEvent) == true } == true).then { game }
    }

    /**
     * 为一个群添加正在进行的游戏
     * @return true 添加成功
     * @return false 添加失败，本群正在进行此类游戏
     */
    fun addGame(groupID: Long, game: AbstractGroupGame): Boolean {
        if(runningGroupGames[groupID] == null){
            runningGroupGames[groupID] = mutableSetOf()
        }
        runningGroupGames[groupID]?.filter { it::class == game::class }?.isNotEmpty()?.then {
            return false // 同类game已存在
        }
        runningGroupGames[groupID]!!.add(game)
        return true
    }

    /**
     * 为一个群移除游戏
     */
    fun removeGame(groupID: Long, game: AbstractGroupGame) {
        runningGroupGames[groupID]?.remove(game)
        if(runningGroupGames[groupID].orEmpty().isEmpty()) {
            runningGroupGames.remove(groupID)
        }
    }
}