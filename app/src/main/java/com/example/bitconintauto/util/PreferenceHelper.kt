package com.example.bitconintauto.util

import android.accessibilityservice.AccessibilityService
import android.content.Context

object PreferenceHelper {
    var accessibilityService: AccessibilityService? = null
    private lateinit var context: Context

    fun init(context: Context) {
        this.context = context
    }

    fun getContext(): Context = context
}
