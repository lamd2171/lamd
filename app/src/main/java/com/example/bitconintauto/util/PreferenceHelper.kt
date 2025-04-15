package com.example.bitconintauto.util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.view.accessibility.AccessibilityService

object PreferenceHelper {
    private lateinit var prefs: SharedPreferences
    var accessibilityService: AccessibilityService? = null

    fun init(context: Context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
    }
}
