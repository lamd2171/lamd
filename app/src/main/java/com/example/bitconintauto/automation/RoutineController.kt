package com.example.bitconintauto.automation

class RoutineController(private val executorManager: ExecutorManager) {

    fun startRoutine(intervalSec: Int = 5) {
        executorManager.startExecution(intervalSec)
    }

    fun stopRoutine() {
        executorManager.stopExecution()
    }

    fun toggleRoutine() {
        if (executorManager.isRunning()) stopRoutine() else startRoutine()
    }

    fun isRunning(): Boolean = executorManager.isRunning()
}
