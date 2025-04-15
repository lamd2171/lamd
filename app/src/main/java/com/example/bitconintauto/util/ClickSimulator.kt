package com.example.bitconintauto.util

import android.accessibilityservice.AccessibilityService
import android.graphics.Bitmap
import android.graphics.Path
import android.os.Build
import android.util.Log
import com.example.bitconintauto.model.Coordinate
import com.example.bitconintauto.ocr.OCRProcessor

class ClickSimulator(private val service: AccessibilityService) {

    fun performClick(coord: Coordinate) {
        val x = coord.x + coord.width / 2
        val y = coord.y + coord.height / 2
        AccessibilityTapper.simulateClick(service, x, y)
    }

    fun clearAndInput(coord: Coordinate, text: String) {
        performClick(coord)
        Thread.sleep(300)
        AccessibilityTextInput.sendText(service, text)
    }

    fun readText(coord: Coordinate): String {
        var result = ""
        ScreenCaptureHelper.capture { bmp ->
            if (bmp != null) {
                val region = OCRCaptureUtils.captureRegion(bmp, coord)
                result = OCRProcessor().getText(region)
            }
        }
        Thread.sleep(300)
        return result
    }

    fun isValueMatched(label1: String, label2: String): Boolean {
        val c1 = CoordinateManager.get(label1).firstOrNull() ?: return false
        val c2 = CoordinateManager.get(label2).firstOrNull() ?: return false

        var v1 = 0.0
        var v2 = 0.0

        ScreenCaptureHelper.capture { bmp ->
            if (bmp != null) {
                val r1 = OCRCaptureUtils.captureRegion(bmp, c1)
                val r2 = OCRCaptureUtils.captureRegion(bmp, c2)
                v1 = OCRProcessor().getText(r1).toDoubleOrNull() ?: 0.0
                v2 = OCRProcessor().getText(r2).toDoubleOrNull() ?: 0.0
            }
        }
        Thread.sleep(300)
        return v1 == v2
    }

    fun scrollToBottom(label: String) {
        val coord = CoordinateManager.get(label).firstOrNull() ?: return
        val startX = coord.x + coord.width / 2
        val startY = coord.y + coord.height - 10
        val endX = startX
        val endY = coord.y + 10
        simulateSwipe(startX, startY, endX, endY)
    }

    fun scrollUntilVisible(targetLabel: String, scrollAreaLabel: String, keyword: String? = null) {
        repeat(6) {
            val coord = CoordinateManager.get(targetLabel).firstOrNull()
            if (coord != null) {
                var bmp: Bitmap? = null
                ScreenCaptureHelper.capture { fullBitmap ->
                    if (fullBitmap != null) {
                        bmp = OCRCaptureUtils.captureRegion(fullBitmap, coord)
                    }
                }
                val result = OCRProcessor().getText(bmp)
                if (keyword == null || result.contains(keyword, true)) {
                    return
                }
            }
            scrollToBottom(scrollAreaLabel)
            Thread.sleep(600)
        }
    }

    private fun simulateSwipe(startX: Int, startY: Int, endX: Int, endY: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val path = Path().apply {
                moveTo(startX.toFloat(), startY.toFloat())
                lineTo(endX.toFloat(), endY.toFloat())
            }

            val gesture = android.accessibilityservice.GestureDescription.Builder()
                .addStroke(android.accessibilityservice.GestureDescription.StrokeDescription(path, 0, 300))
                .build()

            service.dispatchGesture(gesture, null, null)
        }
    }
}
