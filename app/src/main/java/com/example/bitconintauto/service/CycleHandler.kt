package com.example.bitconintauto.service

import android.os.Handler
import android.os.Looper
import android.content.Context

class CycleHandler(
    private val context: Context, // ← 이 줄 추가
    private val intervalMillis: Long,
    private val onLog: (String) -> Unit
) {
    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false

    private val runnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                onLog("자동 실행 중...")
                try {
                    ExecutorManager.start(context)
                } catch (e: Exception) {
                    onLog("오류 발생: ${e.message}")
                }
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
