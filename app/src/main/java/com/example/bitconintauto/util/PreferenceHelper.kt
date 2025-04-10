package com.example.bitconintauto.util

import android.content.Context
import android.content.SharedPreferences

object PreferenceHelper {

    private const val PREF_NAME = "bitconint_prefs"

    fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveString(context: Context, key: String, value: String) {
        getPreferences(context).edit().putString(key, value).apply()
    }

    fun getString(context: Context, key: String): String? {
        return getPreferences(context).getString(key, null)
    }

    fun clear(context: Context) {
        getPreferences(context).edit().clear().apply()
    }
}
