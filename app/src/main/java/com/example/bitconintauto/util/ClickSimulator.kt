package com.example.bitconintauto.util

import android.accessibilityservice.AccessibilityService
import android.graphics.Path
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.bitconintauto.model.Coordinate
import com.example.bitconintauto.ocr.OCRProcessor
import com.example.bitconintauto.util.CoordinateManager.get

class ClickSimulator(val service: AccessibilityService) {

    fun performClick(label: String) {
        val coord = get(label).firstOrNull() ?: return
        performClick(coord)
    }

    fun performClick(coord: Coordinate) {
        val x = coord.x + coord.width / 2
        val y = coord.y + coord.height / 2
        AccessibilityTapper.simulateClick(service, x, y)
    }

    fun clearAndInput(label: String, text: String) {
        val coord = get(label).firstOrNull() ?: return
        clearAndInput(coord, text)
    }

    fun clearAndInput(coord: Coordinate, text: String) {
        performClick(coord)
        Thread.sleep(300)
        AccessibilityTextInput.sendText(service, text)
    }

    fun readText(coord: Coordinate): String {
        val bmp = OCRCaptureUtils.capture(service, coord) ?: return ""
        val result = OCRProcessor().getText(bmp)
        Log.d("ClickSimulator", "[🧪 OCR 결과] ${coord.label} → $result")
        return result
    }

    fun isValueMatched(label1: String, label2: String): Boolean {
        val v1 = get(label1).firstOrNull()?.let { coord ->
            OCRCaptureUtils.capture(service, coord)?.let { bmp -> OCRProcessor().getText(bmp) }?.toDoubleOrNull()
        } ?: return false

        val v2 = get(label2).firstOrNull()?.let { coord ->
            OCRCaptureUtils.capture(service, coord)?.let { bmp -> OCRProcessor().getText(bmp) }?.toDoubleOrNull()
        } ?: return false

        return v1 == v2
    }

    fun scrollToBottom(label: String) {
        val coord = get(label).firstOrNull() ?: return
        val startX = coord.x + coord.width / 2
        val startY = coord.y + coord.height - 10
        val endX = startX
        val endY = coord.y + 10
        simulateSwipe(startX, startY, endX, endY)
    }

    fun scrollUntilVisible(targetLabel: String, scrollAreaLabel: String, keyword: String? = null) {
        repeat(6) {
            val coord = get(targetLabel).firstOrNull()
            if (coord != null) {
                val bmp = OCRCaptureUtils.capture(service, coord) ?: return@repeat
                val result = OCRProcessor().getText(bmp)
                Log.d("ScrollCheck", "[🔍] $targetLabel OCR → $result")
                if (keyword == null || result.contains(keyword, true)) {
                    Log.d("ScrollCheck", "[✅] 타겟 발견: $targetLabel ($result)")
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
            Log.d("ClickSimulator", "[🖐] 스크롤: ($startX, $startY) → ($endX, $endY)")
        }
    }
}