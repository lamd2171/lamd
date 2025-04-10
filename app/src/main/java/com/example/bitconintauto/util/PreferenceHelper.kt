package com.example.bitconintauto.util

import android.content.Context
import android.content.SharedPreferences

object PreferenceHelper {
    private const val PREF_NAME = "BitconintPrefs"
    private const val KEY_INTERVAL = "interval"
    private const val KEY_THRESHOLD = "threshold"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun setInterval(context: Context, interval: Long) {
        getPrefs(context).edit().putLong(KEY_INTERVAL, interval).apply()
    }

    fun getInterval(context: Context): Long {
        return getPrefs(context).getLong(KEY_INTERVAL, 5000)
    }

    fun setThreshold(context: Context, value: Double) {
        getPrefs(context).edit().putFloat(KEY_THRESHOLD, value.toFloat()).apply()
    }

    fun getThreshold(context: Context): Double {
        return getPrefs(context).getFloat(KEY_THRESHOLD, 0.001f).toDouble()
    }
}
