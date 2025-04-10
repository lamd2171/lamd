package com.example.bitconintauto.util

import android.accessibilityservice.AccessibilityService
import android.graphics.Bitmap
import android.util.Log
import com.example.bitconintauto.model.Coordinate

object CycleHandler {

    fun checkAndExecute(
        service: AccessibilityService,
        coordinates: List<Coordinate>,
        threshold: Float = 1.0f,
        onDetected: () -> Unit
    ) {
        for (coord in coordinates) {
            try {
                // 특정 위치 캡처 (별도 캡처 유틸 필요 시 교체 가능)
                val bitmap = ScreenCaptureUtils.capture(service, coord) ?: continue

                // OCR 숫자 인식
                val value = NumberDetector.detectNumberAt(bitmap)
                Log.d("CycleHandler", "좌표(${coord.x}, ${coord.y}) 인식값: $value")

                // 조건 만족 시 콜백
                if (ConditionChecker.shouldTriggerAction(value.toFloat(), threshold)) {
                    onDetected()
                    break
                }

            } catch (e: Exception) {
                Log.e("CycleHandler", "좌표 처리 중 오류: ${e.message}")
            }
        }
    }
}
