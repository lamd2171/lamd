package com.example.bitconintauto.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Build
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

    fun performTextPaste(text: String, target: Coordinate) {
        AutomationClipboard.setText(service, text)
        AutomationUtils.performAutoClick(service, target)
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

        performTextPaste(offsetString, pasteTarget)

        AutomationUtils.performFinalActions(service, CoordinateManager.getFinalActions())
    }
}
