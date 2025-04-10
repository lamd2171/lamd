package com.example.bitconintauto.automation

import android.os.Handler
import android.os.Looper

class ExecutorManager(
    private val cycleHandler: CycleHandler
) {
    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false
    private var intervalMillis: Long = 5000L

    private val executionRunnable = object : Runnable {
        override fun run() {
            if (!isRunning) return
            cycleHandler.executeStep()
            handler.postDelayed(this, intervalMillis)
        }
    }

    fun startExecution(intervalSec: Int = 5) {
        if (isRunning) return
        intervalMillis = intervalSec * 1000L
        isRunning = true
        handler.post(executionRunnable)
    }

    fun stopExecution() {
        isRunning = false
        handler.removeCallbacks(executionRunnable)
    }

    fun isRunning(): Boolean = isRunning
}
