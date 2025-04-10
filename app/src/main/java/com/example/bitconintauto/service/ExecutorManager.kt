package com.example.bitconintauto.service

class ExecutorManager {
    private var cycleHandler: CycleHandler? = null

    fun start(task: () -> Unit, intervalMillis: Long = 5000L) {
        cycleHandler = CycleHandler(intervalMillis, task)
        cycleHandler?.start()
    }

    fun stop() {
        cycleHandler?.stop()
    }
}
