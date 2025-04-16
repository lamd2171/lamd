package com.example.bitconintauto.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.util.Log

class MyAccessibilityService : AccessibilityService() {

    override fun onServiceConnected() {
        Log.d("AccessibilityService", "Service connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // 이벤트 처리 코드
    }

    override fun onInterrupt() {
        // 중단 코드
    }
}
