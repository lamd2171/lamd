package com.example.bitconintauto.util

import android.accessibilityservice.AccessibilityService
import android.util.Log
import com.example.bitconintauto.model.Coordinate
import com.example.bitconintauto.ocr.OCRProcessor

class ClickSimulator(private val service: AccessibilityService) {

    private val ocrProcessor = OCRProcessor()

    fun performClick(coordinate: Coordinate) {
        // GLOBAL_ACTION_CLICK = 1 (상수 대신 직접 사용)
        service.performGlobalAction(1)
        Log.d("ClickSimulator", "Clicked → ${coordinate.label}")
    }

    fun scrollUntilVisible(targetLabel: String, scrollLabel: String) {
        repeat(10) {
            val target = CoordinateManager.get(targetLabel).firstOrNull()
            if (target != null) return
            CoordinateManager.get(scrollLabel).firstOrNull()?.let {
                performClick(it)
                Thread.sleep(400)
            }
        }
    }

    fun clearAndInput(coordinate: Coordinate, text: String) {
        performClick(coordinate)
        Log.d("ClickSimulator", "입력 → $text")
    }

    fun readText(coordinate: Coordinate): String {
        val bmp = ScreenCaptureHelper.captureSync() ?: return ""
        val region = OCRCaptureUtils.captureRegion(bmp, coordinate) ?: return ""
        val text = ocrProcessor.getText(region)
        Log.d("ClickSimulator", "OCR [${coordinate.label}] → $text")
        return text
    }

    fun isValueMatched(label1: String, label2: String): Boolean {
        val coord1 = CoordinateManager.get(label1).firstOrNull() ?: return false
        val coord2 = CoordinateManager.get(label2).firstOrNull() ?: return false

        val bmp = ScreenCaptureHelper.captureSync() ?: return false
        val region1 = OCRCaptureUtils.captureRegion(bmp, coord1) ?: return false
        val region2 = OCRCaptureUtils.captureRegion(bmp, coord2) ?: return false

        val text1 = ocrProcessor.getText(region1)
        val text2 = ocrProcessor.getText(region2)

        return text1.trim() == text2.trim()
    }
}
