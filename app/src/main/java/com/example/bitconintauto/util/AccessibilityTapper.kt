package com.example.bitconintauto.util

import android.accessibilityservice.AccessibilityService
import android.graphics.Path
import android.os.SystemClock
import android.view.MotionEvent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

object AccessibilityTapper {
    fun simulateClick(service: AccessibilityService, x: Int, y: Int) {
        val gesture = android.accessibilityservice.GestureDescription.Builder()
            .addStroke(
                android.accessibilityservice.GestureDescription.StrokeDescription(
                    Path().apply { moveTo(x.toFloat(), y.toFloat()) },
                    0,
                    100
                )
            ).build()
        service.dispatchGesture(gesture, null, null)
    }
}
