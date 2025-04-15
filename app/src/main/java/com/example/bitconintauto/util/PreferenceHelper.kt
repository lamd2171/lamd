package com.example.bitconintauto.util

import android.content.Context
import android.content.SharedPreferences
import android.accessibilityservice.AccessibilityService

object PreferenceHelper {
    private lateinit var prefs: SharedPreferences
    var accessibilityService: AccessibilityService? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences("bitconint_prefs", Context.MODE_PRIVATE)
    }
}
