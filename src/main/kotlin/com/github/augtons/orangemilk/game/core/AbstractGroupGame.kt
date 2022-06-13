package com.github.augtons.orangemilk.game.core

abstract class AbstractGroupGame(
    onFinish: (() -> Unit)? = null
): AbstractGame(onFinish) {

    abstract val players: MutableSet<Long>
    abstract val scores: MutableMap<Long, Double>

    open fun join(newPlayerID: Long) {
        players += newPlayerID
        scores[newPlayerID] = 0.0
    }

    open fun addScore(playerID: Long, scoreToAdd: Double) {
        scores[playerID] = scores[playerID]!! + scoreToAdd
    }
}
