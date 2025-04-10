package com.example.bitconintauto.service

import android.os.Handler
import android.os.Looper

class CycleHandler(private val intervalMillis: Long = 5000L, private val task: () -> Unit) {
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            task()
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
