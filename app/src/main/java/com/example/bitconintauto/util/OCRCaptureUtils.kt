package com.example.bitconintauto.util

import android.accessibilityservice.AccessibilityService
import android.graphics.Bitmap
import android.graphics.Rect
import android.media.ImageReader
import android.view.PixelCopy
import android.view.View
import com.example.bitconintauto.model.Coordinate

object OCRCaptureUtils {
    fun capture(service: AccessibilityService, coord: Coordinate): Bitmap? {
        // 이 부분은 실제 MediaProjection or Screenshot 권한이 필요
        // 지금은 null 반환하도록 처리 (옵션 기능)
        return null
    }

    fun captureRegion(coord: Coordinate): Bitmap? {
        // 캡처 영역 직접 사용하는 버전도 null 처리
        return null
    }
}
