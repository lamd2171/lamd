package com.example.bitconintauto.util

import android.accessibilityservice.AccessibilityService
import android.graphics.Path
import android.graphics.Rect
import android.os.Build
import android.util.Log
import com.example.bitconintauto.model.Coordinate
import com.example.bitconintauto.ocr.OCRProcessor

class ClickSimulator(val service: AccessibilityService) {

    fun performClick(label: String) {
        val coord = CoordinateManager.get(label).firstOrNull()
        if (coord == null) {
            Log.w("ClickSimulator", "[❌] '$label' 좌표가 존재하지 않습니다.")
            return
        }
        performClick(coord)
    }

    fun performClick(coord: Coordinate) {
        val x = coord.x + coord.width / 2
        val y = coord.y + coord.height / 2
        Log.d("ClickSimulator", "[🖱️] 클릭 위치: (${coord.label}) $x, $y")
        AccessibilityTapper.simulateClick(service, x, y)
    }

    fun clearAndInput(label: String, text: String) {
        val coord = CoordinateManager.get(label).firstOrNull()
        if (coord == null) {
            Log.w("ClickSimulator", "[❌] '$label' 입력 대상 없음")
            return
        }
        clearAndInput(coord, text)
    }

    fun clearAndInput(coord: Coordinate, text: String) {
        performClick(coord)
        Thread.sleep(200)
        Log.d("ClickSimulator", "[⌨️] 입력: '$text' at ${coord.label}")
        AccessibilityTextInput.sendText(service, text)
    }

    fun readText(coord: Coordinate): String {
        val bitmap = OCRCaptureUtils.capture(service, coord)
        if (bitmap == null) {
            Log.w("ClickSimulator", "[❌] OCR 캡처 실패 at ${coord.label}")
            return ""
        }
        val result = OCRProcessor().getText(bitmap).trim()
        Log.d("ClickSimulator", "[🔍] OCR(${coord.label}): '$result'")
        return result
    }

    fun isValueMatched(label1: String, label2: String): Boolean {
        val v1 = CoordinateManager.get(label1).firstOrNull()?.let { coord1 ->
            val bmp = OCRCaptureUtils.capture(service, coord1)
            val text = bmp?.let { OCRProcessor().getText(it).trim() } ?: ""
            Log.d("ClickSimulator", "[📐] OCR($label1): '$text'")
            text.toDoubleOrNull()
        } ?: return false

        val v2 = CoordinateManager.get(label2).firstOrNull()?.let { coord2 ->
            val bmp = OCRCaptureUtils.capture(service, coord2)
            val text = bmp?.let { OCRProcessor().getText(it).trim() } ?: ""
            Log.d("ClickSimulator", "[📐] OCR($label2): '$text'")
            text.toDoubleOrNull()
        } ?: return false

        Log.d("ClickSimulator", "[⚖️] 비교: $v1 vs $v2")
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

    fun scrollTo(label: String, targetLabel: String) {
        repeat(3) {
            scrollToBottom(label)
            Thread.sleep(500)
        }
    }

    fun scrollUntilVisible(targetLabel: String, scrollAreaLabel: String) {
        repeat(5) {
            val target = CoordinateManager.get(targetLabel).firstOrNull()
            if (target != null) {
                Log.d("ClickSimulator", "[👀] 타겟 발견됨: $targetLabel")
                return
            }
            scrollToBottom(scrollAreaLabel)
            Thread.sleep(500)
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
            Log.d("ClickSimulator", "[🖐] 스크롤 제스처 실행: $startX,$startY → $endX,$endY")
        }
    }
}