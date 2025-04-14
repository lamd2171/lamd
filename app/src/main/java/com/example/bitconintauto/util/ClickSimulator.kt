package com.example.bitconintauto.util

import android.accessibilityservice.AccessibilityService
import com.example.bitconintauto.model.Coordinate
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
        AccessibilityTextInput.sendText(service, text)
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

    fun readText(coord: Coordinate): String {
        val bitmap = OCRCaptureUtils.capture(service, coord) ?: return ""
        return OCRProcessor().getText(bitmap)
    }

    fun scrollTo(label: String, targetLabel: String) {
        // 실제 앱에서 필요한 경우 구현, 없으면 생략
    }

    fun scrollToBottom(label: String) {
        // 실제 앱에서 필요한 경우 구현, 없으면 생략
    }
}
