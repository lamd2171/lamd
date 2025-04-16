// app/src/main/java/com/example/bitconintauto/util/ClickSimulator.kt
package com.example.bitconintauto.util

import android.accessibilityservice.AccessibilityService
import android.graphics.Path
import android.os.SystemClock
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.graphics.Bitmap
import com.example.bitconintauto.model.Coordinate
import com.example.bitconintauto.ocr.OCRProcessor
import android.accessibilityservice.GestureDescription

class ClickSimulator(private val service: AccessibilityService) {

    private val ocrProcessor = OCRProcessor()

    fun performClick(coord: Coordinate) {
        val path = Path().apply {
            moveTo((coord.x + coord.width / 2).toFloat(), (coord.y + coord.height / 2).toFloat())
        }

        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 100))
            .build()

        service.dispatchGesture(gesture, null, null)

        Log.d("ClickSimulator", "Clicked: ${coord.label}")
    }

    fun readText(coord: Coordinate): String {
        val bmp = ScreenCaptureHelper.captureSync() ?: return ""
        val region = OCRCaptureUtils.captureRegion(bmp, coord)
        return ocrProcessor.getText(region ?: return "")
    }

    fun clearAndInput(coord: Coordinate, text: String) {
        performClick(coord)
        SystemClock.sleep(300)
        InputHelper.inputText(service.rootInActiveWindow, text)
    }

    fun isValueMatched(label1: String, label2: String): Boolean {
        val coord1 = CoordinateManager.get(label1).firstOrNull() ?: return false
        val coord2 = CoordinateManager.get(label2).firstOrNull() ?: return false

        val bmp = ScreenCaptureHelper.captureSync() ?: return false
        val text1 = ocrProcessor.getText(OCRCaptureUtils.captureRegion(bmp, coord1) ?: return false)
        val text2 = ocrProcessor.getText(OCRCaptureUtils.captureRegion(bmp, coord2) ?: return false)

        return text1.trim() == text2.trim()
    }

    fun scrollUntilVisible(targetLabel: String, scrollAreaLabel: String) {
        val target = CoordinateManager.get(targetLabel).firstOrNull() ?: return
        val area = CoordinateManager.get(scrollAreaLabel).firstOrNull() ?: return

        repeat(5) {
            val text = readText(target)
            if (text.isNotBlank()) return
            performClick(area)
            SystemClock.sleep(500)
        }
    }
}
