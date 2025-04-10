package com.example.bitconintauto.util

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.SharedPreferences

object PreferenceHelper {

    private const val PREF_NAME = "bitconint_prefs"
    var accessibilityService: AccessibilityService? = null

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

    // 서비스 저장 메서드
    fun saveService(service: AccessibilityService) {
        accessibilityService = service
    }

    // 서비스 클리어 메서드
    fun clearService() {
        accessibilityService = null
    }
}
