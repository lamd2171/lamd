package com.example.bitconintauto.logic

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.example.bitconintauto.service.AutoClicker
import com.example.bitconintauto.service.OcrProcessor

class ExecutorManager(private val context: Context) {

    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false
    private var intervalMillis: Long = 5000 // 기본 5초 간격

    private val cycleTask = object : Runnable {
        override fun run() {
            if (!isRunning) return

            try {
                AutoClicker.executeCycle(context)
            } catch (e: Exception) {
                Toast.makeText(context, "자동 실행 중 오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
            }

            handler.postDelayed(this, intervalMillis)
        }
    }

    fun start(intervalSec: Int = 5) {
        if (isRunning) return
        intervalMillis = (intervalSec * 1000).toLong()
        isRunning = true
        handler.post(cycleTask)
        Toast.makeText(context, "자동 실행 시작", Toast.LENGTH_SHORT).show()
    }

    fun stop() {
        isRunning = false
        handler.removeCallbacks(cycleTask)
        Toast.makeText(context, "자동 실행 중지", Toast.LENGTH_SHORT).show()
    }

    fun isRunning(): Boolean {
        return isRunning
    }
}
