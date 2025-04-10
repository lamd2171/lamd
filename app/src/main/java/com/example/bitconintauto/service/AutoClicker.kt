package com.example.bitconintauto.service

import android.accessibilityservice.AccessibilityService
import android.graphics.Path
import android.os.Build
import android.view.accessibility.AccessibilityNodeInfo
import android.accessibilityservice.GestureDescription
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
        val args = android.os.Bundle()
        args.putCharSequence(
            AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
            text
        )
        node?.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun executeCycle(x: Int, y: Int, text: String, targetNode: AccessibilityNodeInfo?) {
        performClick(x, y)
        performTextPaste(targetNode, text)
    }
}
