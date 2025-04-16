package com.example.bitconintauto.util

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent

object PreferenceHelper {
    lateinit var context: Context
    var accessibilityService: AccessibilityService? = null

    var resultCode: Int = 0
    var resultData: Intent? = null

    var debugMode: Boolean = true

    fun init(ctx: Context) {
        context = ctx
    }
}
