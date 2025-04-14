package com.example.bitconintauto.util

import android.accessibilityservice.AccessibilityService
import android.graphics.Path
import android.graphics.Rect
import android.os.Build
import android.util.Log
import com.example.bitconintauto.model.Coordinate
import com.example.bitconintauto.ocr.OCRProcessor
import kotlinx.coroutines.*

class ClickSimulator(val service: AccessibilityService) {

    fun performClick(label: String) {
        val coord = CoordinateManager.get(label).firstOrNull() ?: return
        performClick(coord)
    }

    fun performClick(coord: Coordinate) {
        val x = coord.x + coord.width / 2
        val y = coord.y + coord.height / 2
        AccessibilityTapper.simulateClick(service, x, y)
        Log.d("ClickSimulator", "[👆 Click] (${coord.label}) at ($x,$y)")
    }

    fun clearAndInput(label: String, text: String) {
        val coord = CoordinateManager.get(label).firstOrNull() ?: return
        clearAndInput(coord, text)
    }

    fun clearAndInput(coord: Coordinate, text: String) {
        performClick(coord)
        Thread.sleep(200)
        AccessibilityTextInput.sendText(service, text)
        Log.d("ClickSimulator", "[✍ 입력] ${coord.label} → $text")
    }

    fun readText(coord: Coordinate): String {
        val bitmap = OCRCaptureUtils.capture(service, coord) ?: return ""
        val text = OCRProcessor().getText(bitmap)
        Log.d("ClickSimulator", "[🔍 OCR] ${coord.label} → $text")
        return text
    }

    fun isValueMatched(label1: String, label2: String): Boolean {
        val coord1 = CoordinateManager.get(label1).firstOrNull() ?: return false
        val coord2 = CoordinateManager.get(label2).firstOrNull() ?: return false

        val bmp1 = OCRCaptureUtils.capture(service, coord1)
        val bmp2 = OCRCaptureUtils.capture(service, coord2)
        val v1 = bmp1?.let { OCRProcessor().getText(it) }?.toDoubleOrNull()
        val v2 = bmp2?.let { OCRProcessor().getText(it) }?.toDoubleOrNull()

        Log.d("ClickSimulator", "[⚖ 비교] $v1 vs $v2")
        return v1 != null && v2 != null && v1 == v2
    }

    fun scrollToBottom(label: String) {
        val coord = CoordinateManager.get(label).firstOrNull() ?: return
        val startX = coord.x + coord.width / 2
        val startY = coord.y + coord.height - 10
        val endX = startX
        val endY = coord.y + 10

        simulateSwipe(startX, startY, endX, endY)
    }

    fun scrollUntilVisible(targetLabel: String, scrollAreaLabel: String) {
        repeat(10) {
            val coord = CoordinateManager.get(targetLabel).firstOrNull()
            if (coord != null) {
                val bitmap = OCRCaptureUtils.capture(service, coord) ?: return
                val ocrText = OCRProcessor().getText(bitmap)
                if (ocrText.contains("Sellable", true)) {
                    Log.d("ClickSimulator", "[✅ 텍스트 발견] $ocrText")
                    return
                }
            }
            scrollToBottom(scrollAreaLabel)
            Thread.sleep(500)
        }
        Log.w("ClickSimulator", "[❌ 텍스트 미발견] $targetLabel")
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
            Log.d("ClickSimulator", "[🖐 Scroll] $startX,$startY → $endX,$endY")
        }
    }
}
