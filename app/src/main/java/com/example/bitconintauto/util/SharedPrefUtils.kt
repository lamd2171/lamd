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

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
}
