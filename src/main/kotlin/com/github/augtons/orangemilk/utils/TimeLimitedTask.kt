package com.github.augtons.orangemilk.utils

class TimeLimitedTask(
    val taskCountLimits: Int = 20,
    val timeLimits: Long = 60_000
){

    private var firstHappedTaskMS: Long = 0
    private var happendCounts: Int = 0

    suspend fun happenSuspend(todo: suspend () -> Unit) {
        val current = System.currentTimeMillis()
        if (current - firstHappedTaskMS >= timeLimits) {
            firstHappedTaskMS = current
            happendCounts = 1
        }

        if (happendCounts <= taskCountLimits) {
            todo()
            happendCounts++
        }
    }

    fun happen(todo: () -> Unit): Boolean {
        val current = System.currentTimeMillis()
        if (current - firstHappedTaskMS >= timeLimits) {
            firstHappedTaskMS = current
            happendCounts = 1
        }

        if (happendCounts <= taskCountLimits) {
            todo()
            happendCounts++
            return true
        }else {
            return false
        }
    }

    fun happen(todo: () -> Unit, elseTodo: (() -> Unit)) {
        if (!happen(todo)) {
            elseTodo()
        }
    }
}