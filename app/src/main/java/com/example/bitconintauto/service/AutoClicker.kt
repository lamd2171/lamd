package com.example.bitconintauto.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Build
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.RequiresApi

class AutoClicker(private val service: AccessibilityService) {

    @RequiresApi(Build.VERSION_CODES.N)
    fun performClick(x: Int, y: Int) {
        val path = Path().apply {
            moveTo(x.toFloat(), y.toFloat())
        }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 100))
            .build()
        service.dispatchGesture(gesture, null, null)
    }

    fun performTextPaste(node: AccessibilityNodeInfo?, text: String) {
        node?.performAction(AccessibilityNodeInfo.ACTION_FOCUS)
        val arguments = android.os.Bundle()
        arguments.putCharSequence(
            AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
            text
        )
        node?.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun executeCycle(x: Int, y: Int, text: String, targetNode: AccessibilityNodeInfo?) {
        performClick(x, y)
        performTextPaste(targetNode, text)
    }
}
