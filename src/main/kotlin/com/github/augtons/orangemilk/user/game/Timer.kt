package com.github.augtons.orangemilk.user.game

import kotlinx.coroutines.*

class Timer(
    @set:Synchronized var time: Int = 120,
    val onAlarm: () -> Unit
) {
    // 不要在定时器运行时改变这个map
    // 如果想改也行，封装新函数将对此map的增删异步化，但是我现在觉得还没必要做
    val timeWachers = mutableMapOf<Int, suspend () -> Unit>()

    val coroutineScope = CoroutineScope(Dispatchers.IO)
    var runningJob: Job? = null

    fun start() {
        runningJob?.cancel()
        runningJob = coroutineScope.launch {
            do {
                timeWachers[time]?.invoke()
                delay(1000)
            } while (time-- > 0)
            onAlarm()
            coroutineScope.cancel()
        }
    }
}