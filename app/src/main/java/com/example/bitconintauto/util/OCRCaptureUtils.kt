package com.example.bitconintauto.util

import android.accessibilityservice.AccessibilityService
import android.graphics.Bitmap
import android.graphics.Rect
import com.example.bitconintauto.model.Coordinate

object OCRCaptureUtils {

    /**
     * OCR 감지용 캡처 영역을 Coordinate의 width/height 기반으로 생성
     */
    fun capture(service: AccessibilityService, coord: Coordinate): Bitmap? {
        // 1. 캡처 영역 계산
        val captureWidth = coord.width.takeIf { it > 0 } ?: 80
        val captureHeight = coord.height.takeIf { it > 0 } ?: 40

        val left = coord.x - (captureWidth / 2)
        val top = coord.y - (captureHeight / 2)
        val right = coord.x + (captureWidth / 2)
        val bottom = coord.y + (captureHeight / 2)

        val region = Rect(left, top, right, bottom)

        // 2. 실제 구현에서는 AccessibilityService에서 화면을 캡처하여 region 영역만 잘라야 함
        // 여기서는 테스트용 빈 Bitmap 생성 (실제는 MediaProjection이나 PixelCopy로 대체)
        return try {
            Bitmap.createBitmap(region.width(), region.height(), Bitmap.Config.ARGB_8888)
        } catch (e: Exception) {
            null
