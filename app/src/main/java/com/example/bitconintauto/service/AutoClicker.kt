package com.example.bitconintauto.service

import android.accessibilityservice.AccessibilityService
import android.graphics.Path
import android.os.Handler
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.GestureDescription
import android.view.accessibility.AccessibilityNodeInfo

class AutoClicker(private val service: AccessibilityService) {

    fun performClick(x: Int, y: Int) {
        val path = Path().apply { moveTo(x.toFloat(), y.toFloat()) }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 100))
            .build()

        service.dispatchGesture(gesture, null, null)
    }

    fun performTextPaste(node: AccessibilityNodeInfo?, text: String) {
        node?.performAction(AccessibilityNodeInfo.ACTION_FOCUS)
        val arguments = android.os.Bundle()
        arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text)
        node?.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
    }
}
