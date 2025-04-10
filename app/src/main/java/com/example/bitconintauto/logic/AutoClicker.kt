package com.example.bitconintauto.logic

import android.accessibilityservice.AccessibilityService
import android.graphics.Path
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class AutoClicker(private val service: AccessibilityService) {

    fun performClick(x: Int, y: Int) {
        val path = Path().apply {
            moveTo(x.toFloat(), y.toFloat())
        }

        val gesture = android.accessibilityservice.GestureDescription.Builder()
            .addStroke(android.accessibilityservice.GestureDescription.StrokeDescription(path, 0, 100))
            .build()

        service.dispatchGesture(gesture, null, null)
    }

    fun clickWithDelay(x: Int, y: Int, delayMillis: Long) {
        Handler(Looper.getMainLooper()).postDelayed({
            performClick(x, y)
        }, delayMillis)
    }
}
