package com.example.bitconintauto.util

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.graphics.Bitmap
import android.util.Log
import com.example.bitconintauto.model.Coordinate
import com.example.bitconintauto.ocr.OCRProcessor
import kotlinx.coroutines.delay

class ClickSimulator(private val service: AccessibilityService) {

    private val ocrProcessor = OCRProcessor()

    // 클릭 처리
    fun performClick(coordinate: Coordinate) {
        // 정확한 글로벌 액션 클릭 처리
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_CLICK)
        Log.d("ClickSimulator", "Clicked → ${coordinate.label}")
    }

    // 롱클릭 처리
    fun longClick(service: AccessibilityService, x: Int, y: Int) {
        // 롱클릭 처리 (필요시 추가 구현)
    }

    // 텍스트 입력 처리
    fun inputText(service: AccessibilityService, text: String) {
        // 텍스트 입력 처리 (필요시 추가 구현)
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
        longClick(service, coordinate.centerX(), coordinate.centerY())
        inputText(service, text)
        Log.d("ClickSimulator", "입력 → $text")
    }

    // 수정된 부분: captureSync()에서 Bitmap?를 처리하고, 반환값에 맞게 수정
    fun isValueMatched(label1: String, label2: String): Boolean {
        val coord1 = CoordinateManager.get(label1).firstOrNull() ?: return false
        val coord2 = CoordinateManager.get(label2).firstOrNull() ?: return false

        val bmp = ScreenCaptureHelper.captureSync() ?: return false  // 캡처 실패시 false 반환
        val text1 = ocrProcessor.getText(OCRCaptureUtils.captureRegion(bmp, coord1) ?: return "")
        val text2 = ocrProcessor.getText(OCRCaptureUtils.captureRegion(bmp, coord2) ?: return "")

        return text1.trim() == text2.trim()
    }

    fun readText(coordinate: Coordinate): String {
        val bmp = ScreenCaptureHelper.captureSync() ?: return ""
        val region = OCRCaptureUtils.captureRegion(bmp, coordinate) ?: return ""
        val text = ocrProcessor.getText(region)
        Log.d("ClickSimulator", "OCR [${coordinate.label}] → $text")
        return text
    }
}
