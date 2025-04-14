package com.example.bitconintauto.util

import android.content.Context
import android.content.SharedPreferences

object SharedPrefUtils {
    private const val PREF_NAME = "bitconint_auto_pref"
    private const val KEY_FIRST_RUN = "is_first_run"

    // ✅ 앱 최초 실행 여부 확인
    fun isFirstRun(context: Context): Boolean {
        val prefs = getPrefs(context)
        return prefs.getBoolean(KEY_FIRST_RUN, true)
    }

    // ✅ 튜토리얼 완료 상태 저장
    fun setFirstRunComplete(context: Context) {
        val prefs = getPrefs(context)
        prefs.edit().putBoolean(KEY_FIRST_RUN, false).apply()
    }

    // ✅ 테스트 및 개발용 리셋 (필요 시 호출)
    fun resetFirstRun(context: Context) {
        val prefs = getPrefs(context)
        prefs.edit().putBoolean(KEY_FIRST_RUN, true).apply()
    }

    // SharedPrefUtils.kt 내에 추가
    fun init(context: Context) {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    }

    fun loadFromPrefs(context: Context) {
        val json = preferences.getString("coordinates", null) ?: return
        CoordinateManager.loadFromJson(json)
    }

    fun setDebugMode(enabled: Boolean) {
        preferences.edit().putBoolean("debug_mode", enabled).apply()
    }

    fun isDebugMode(): Boolean {
        return preferences.getBoolean("debug_mode", false)
    }


    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
}
