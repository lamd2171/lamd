// ✅ ClickSimulator.kt - OCR 기반 클릭 기능 포함 전체 완성본

package com.example.bitconintauto.util

import android.accessibilityservice.AccessibilityService
import android.graphics.Path
import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.RequiresApi
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
        Log.d("ClickSimulator", "[\uD83D\uDC46] 좌표 클릭: (${x}, ${y})")
        AccessibilityTapper.simulateClick(service, x, y)
    }

    fun clearAndInput(label: String, text: String) {
        val coord = CoordinateManager.get(label).firstOrNull() ?: return
        clearAndInput(coord, text)
    }

    fun clearAndInput(coord: Coordinate, text: String) {
        performClick(coord)
        Thread.sleep(200)
        AccessibilityTextInput.sendText(service, text)
    }

    fun readText(coord: Coordinate): String {
        val bitmap = OCRCaptureUtils.capture(service, coord) ?: return ""
        val text = OCRProcessor().getText(bitmap).trim()
        Log.d("ClickSimulator", "[\uD83D\uDD0D] OCR 텍스트 추출 결과: $text")
        return text
    }

    fun isValueMatched(label1: String, label2: String): Boolean {
        val v1 = CoordinateManager.get(label1).firstOrNull()?.let { coord1 ->
            OCRCaptureUtils.capture(service, coord1)?.let { bmp -> OCRProcessor().getText(bmp) }?.toDoubleOrNull()
        } ?: return false
        val v2 = CoordinateManager.get(label2).firstOrNull()?.let { coord2 ->
            OCRCaptureUtils.capture(service, coord2)?.let { bmp -> OCRProcessor().getText(bmp) }?.toDoubleOrNull()
        } ?: return false
        Log.d("ClickSimulator", "[\uD83D\uDCCA] 비교 값 → v1: $v1, v2: $v2")
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
            val coord = CoordinateManager.get(targetLabel).firstOrNull()
            if (coord != null) {
                Log.d("ClickSimulator", "[\uD83D\uDC40] '$targetLabel' 감지됨")
                return
            }
            Log.d("ClickSimulator", "[⬇️] '$targetLabel' 미감지 → 스크롤 시도 ($it)")
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
            Log.d("ClickSimulator", "[\uD83D\uDD50] 스크롤 제스처 실행: $startX,$startY → $endX,$endY")
        }
    }

    // ✅ OCR 텍스트 기반 클릭 기능 추가
    fun clickIfTextFound(label: String, targetText: String) {
        val coord = CoordinateManager.get(label).firstOrNull() ?: return
        val bitmap = OCRCaptureUtils.capture(service, coord) ?: return
        val text = OCRProcessor().getText(bitmap).trim()

        Log.d("ClickSimulator", "[\uD83D\uDD0D] '$label'에서 OCR 텍스트 감지: '$text' vs '$targetText'")
        if (text.contains(targetText, ignoreCase = true)) {
            Log.d("ClickSimulator", "[✅] '$targetText' 감지됨 → 클릭 실행")
            performClick(coord)
        } else {
            Log.w("ClickSimulator", "[❌] '$targetText' 감지 안됨 → 클릭 생략")
        }
    }
}
