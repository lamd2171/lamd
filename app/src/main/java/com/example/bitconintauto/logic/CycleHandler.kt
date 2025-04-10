package com.example.bitconintauto.util

import android.accessibilityservice.AccessibilityService
import android.graphics.Bitmap
import android.util.Log
import com.example.bitconintauto.model.Coordinate
import com.example.bitconintauto.util.NumberDetector
import com.example.bitconintauto.util.ConditionChecker
import com.example.bitconintauto.util.OCRCaptureUtils

object CycleHandler {

    fun checkAndExecute(
        service: AccessibilityService,
        coordinates: List<Coordinate>,
        threshold: Float = 1.0f,
        onDetected: () -> Unit
    ) {
        for (coord in coordinates) {
            try {
                // 현재 좌표 기준 화면 영역 캡처
                val bitmap: Bitmap = OCRCaptureUtils.capture(service, coord)
                    ?: continue

                // OCR 인식
                val value = NumberDetector.detectNumberAt(bitmap)
                Log.d("CycleHandler", "인식된 값 (${coord.x}, ${coord.y}) = $value")

                // 조건 만족 시 콜백
                if (ConditionChecker.shouldTriggerAction(value.toFloat(), threshold)) {
                    onDetected()
                    break
                }

            } catch (e: Exception) {
                Log.e("CycleHandler", "OCR 수행 중 오류: ${e.message}")
            }
        }
    }
}
