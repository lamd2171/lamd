package com.example.bitconintauto.logic

import android.accessibilityservice.AccessibilityService
import android.os.Handler
import android.os.Looper
import com.example.bitconintauto.service.AutoClicker

class ExecutorManager(private val service: AccessibilityService) {

    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false

    fun startExecution(x: Int, y: Int, text: String, targetNode: android.view.accessibility.AccessibilityNodeInfo?) {
        if (isRunning) return
        isRunning = true
        runCycle(x, y, text, targetNode)
    }

    fun stopExecution() {
        isRunning = false
        handler.removeCallbacksAndMessages(null)
    }

    private fun runCycle(x: Int, y: Int, text: String, targetNode: android.view.accessibility.AccessibilityNodeInfo?) {
        if (!isRunning) return

        val autoClicker = AutoClicker(service)
        autoClicker.executeCycle(x, y, text, targetNode)

        handler.postDelayed({
            runCycle(x, y, text, targetNode)
        }, 3000L)
    }
}
