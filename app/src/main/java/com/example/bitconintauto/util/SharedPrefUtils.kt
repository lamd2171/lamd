package com.example.bitconintauto.util

import android.content.Context
import android.content.SharedPreferences

object SharedPrefUtils {
    private const val PREF_NAME = "bitconint_auto_pref"
    private const val KEY_FIRST_RUN = "is_first_run"

    fun isFirstRun(context: Context): Boolean {
        val prefs = getPrefs(context)
        return prefs.getBoolean(KEY_FIRST_RUN, true)
    }

    fun setFirstRunComplete(context: Context) {
        val prefs = getPrefs(context)
        prefs.edit().putBoolean(KEY_FIRST_RUN, false).apply()
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
}
