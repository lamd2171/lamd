package com.example.bitconintauto.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Build
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.RequiresApi
import com.example.bitconintauto.model.Coordinate
import com.example.bitconintauto.util.AutomationClipboard
import com.example.bitconintauto.util.AutomationUtils
import com.example.bitconintauto.util.CoordinateManager
import com.example.bitconintauto.util.Utils

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
        val args = android.os.Bundle().apply {
            putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text)
        }
        node?.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun executeCycle(
        clickPath: List<Coordinate>,
        copyTarget: Coordinate,
        offset: Float,
        pasteTarget: Coordinate
    ) {
        clickPath.forEach {
            AutomationUtils.performAutoClick(service, it)
            Thread.sleep(500)
        }

        val copiedValue = Utils.readValueAt(service, copyTarget)
        val offsetValue = copiedValue?.plus(offset) ?: return
        val offsetString = offsetValue.toString()

        AutomationUtils.pasteValueAt(service, offsetString, pasteTarget)

        // 마지막 액션들 수행 (예: 전송, 확인 버튼)
        AutomationUtils.performFinalActions(service, CoordinateManager.getFinalActions())
    }
}
