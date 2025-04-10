package com.example.bitconintauto.service

import android.os.Handler
import android.os.Looper

class CycleHandler(
    private val intervalMillis: Long,
    private val onLog: (String) -> Unit
) {
    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false

    private val runnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                onLog("자동 실행 중...")
                ExecutorManager.start()  // ExecutorManager 싱글톤 사용
                handler.postDelayed(this, intervalMillis)
            }
        }
    }

    fun start() {
        if (!isRunning) {
            isRunning = true
            onLog("자동 실행 시작됨")
            handler.post(runnable)
        }
    }

    fun stop() {
        if (isRunning) {
            isRunning = false
            onLog("자동 실행 중지됨")
            handler.removeCallbacks(runnable)
        }
    }

    fun isRunning(): Boolean = isRunning
}
