package com.example.bitconintauto.core

import android.accessibilityservice.AccessibilityService
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent

object AutoClicker {
    fun click(service: AccessibilityService, x: Int, y: Int) {
        val path = Path().apply {
            moveTo(x.toFloat(), y.toFloat())
        }

        val gesture = android.accessibilityservice.GestureDescription.Builder()
            .addStroke(android.accessibilityservice.GestureDescription.StrokeDescription(path, 0, 100))
            .build()

        service.dispatchGesture(gesture, null, null)
    }

    fun clickCoordinate(service: AccessibilityService, coordinate: Coordinate) {
        click(service, coordinate.x, coordinate.y)
    }
}
