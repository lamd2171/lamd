package com.example.bitconintauto.util

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.SharedPreferences

object PreferenceHelper {

    private const val PREF_NAME = "bitconint_prefs"
    private const val KEY_TUTORIAL_SEEN = "tutorial_seen"

    var accessibilityService: AccessibilityService? = null

    // ✅ 튜토리얼 체크 관련 init & 키 사용
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun isTutorialSeen(): Boolean {
        return prefs.getBoolean(KEY_TUTORIAL_SEEN, false)
    }

    fun setTutorialSeen(value: Boolean) {
        prefs.edit().putBoolean(KEY_TUTORIAL_SEEN, value).apply()
    }

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
