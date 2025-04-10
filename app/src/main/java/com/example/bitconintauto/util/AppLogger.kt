package com.example.bitconintauto.util

import android.util.Log

object AppLogger {
    private const val TAG = "BitconintAuto"

    fun log(message: String) {
        Log.d(TAG, message)
    }
}
