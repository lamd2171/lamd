package com.example.bitconintauto.logic

import android.os.Handler
import android.os.Looper

class ExecutorManager {

    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false
    private var intervalMillis: Long = 3000L

    fun setInterval(seconds: Int) {
        intervalMillis = (if (seconds < 1) 1 else seconds) * 1000L
    }

    fun startCycle(onExecute: () -> Unit) {
        if (isRunning) return
        isRunning = true
        runLoop(onExecute)
    }

    fun stopCycle() {
        isRunning = false
        handler.removeCallbacksAndMessages(null)
    }

    private fun runLoop(onExecute: () -> Unit) {
        if (!isRunning) return

        onExecute()

        handler.postDelayed({
            runLoop(onExecute)
        }, intervalMillis)
    }
}
