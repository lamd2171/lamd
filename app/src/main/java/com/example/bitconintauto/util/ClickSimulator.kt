package com.example.bitconintauto.util

import android.accessibilityservice.AccessibilityService
import android.graphics.Path
import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.RequiresApi
import com.example.bitconintauto.model.Coordinate
import kotlinx.coroutines.*
import com.example.bitconintauto.ocr.OCRProcessor

class ClickSimulator(private val service: AccessibilityService) {

    fun performClick(label: String) {
        val coord = CoordinateManager.get(label).firstOrNull() ?: return
        performClick(coord)
    }

    fun performClick(coord: Coordinate) {
        val x = coord.x + coord.width / 2
        val y = coord.y + coord.height / 2
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
        return OCRProcessor().getText(bitmap)
    }

    fun isValueMatched(label1: String, label2: String): Boolean {
        val v1 = CoordinateManager.get(label1).firstOrNull()?.let { coord1 ->
            OCRCaptureUtils.capture(service, coord1)?.let { bmp -> OCRProcessor().getText(bmp) }?.toDoubleOrNull()
        } ?: return false
        val v2 = CoordinateManager.get(label2).firstOrNull()?.let { coord2 ->
            OCRCaptureUtils.capture(service, coord2)?.let { bmp -> OCRProcessor().getText(bmp) }?.toDoubleOrNull()
        } ?: return false
        return v1 == v2
    }

    // ✅ 아래는 실제 동작하는 스크롤 제스처 구현부

    fun scrollToBottom(label: String) {
        val coord = CoordinateManager.get(label).firstOrNull() ?: return
        val startX = coord.x + coord.width / 2
        val startY = coord.y + coord.height - 10
        val endX = startX
        val endY = coord.y + 10

        simulateSwipe(startX, startY, endX, endY)
    }

    fun scrollTo(label: String, targetLabel: String) {
        // 아직 타겟 요소 탐지 로직은 미구현 상태 (OCR이나 Node 탐색 필요)
        // 대신 스크롤 여러번 시도하도록 구성
        repeat(3) {
            scrollToBottom(label)
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
