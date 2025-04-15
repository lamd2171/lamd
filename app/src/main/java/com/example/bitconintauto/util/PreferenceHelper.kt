package com.example.bitconintauto.util

import android.content.Context
import android.content.SharedPreferences
import android.accessibilityservice.AccessibilityService
import android.preference.PreferenceManager


object PreferenceHelper {
    private lateinit var prefs: SharedPreferences
    var accessibilityService: AccessibilityService? = null

    fun init(context: Context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
    }
}
