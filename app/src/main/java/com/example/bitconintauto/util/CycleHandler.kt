package com.example.bitconintauto.util

import android.os.Handler
import android.os.Looper

class CycleHandler(private val intervalMillis: Long, private val task: () -> Unit) {

    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            task.invoke()
            handler.postDelayed(this, intervalMillis)
        }
    }

    fun start() {
        stop()
        handler.post(runnable)
    }

    fun stop() {
        handler.removeCallbacks(runnable)
    }
}
