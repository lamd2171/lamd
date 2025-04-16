package com.example.bitconintauto.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import com.example.bitconintauto.util.PreferenceHelper

class MyAccessibilityService : AccessibilityService() {

    override fun onServiceConnected() {
        PreferenceHelper.accessibilityService = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {}
}
