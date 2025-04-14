package com.example.bitconintauto.util

import android.accessibilityservice.AccessibilityService
import android.graphics.*
import com.example.bitconintauto.model.Coordinate

object OCRCaptureUtils {

    // 실제 캡처를 할 수 있는 구조가 아니므로 빈 비트맵만 반환 (샘플 구조 유지)
    fun capture(service: AccessibilityService, coord: Coordinate): Bitmap {
        val bitmap = Bitmap.createBitmap(coord.width, coord.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 42f
            isAntiAlias = true
        }

        canvas.drawColor(Color.WHITE)
        canvas.drawText("", 10f, coord.height / 2f, paint)

        return bitmap
    }

    fun captureRegion(coord: Coordinate): Bitmap {
        val bitmap = Bitmap.createBitmap(coord.width, coord.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 42f
            isAntiAlias = true
        }

        canvas.drawColor(Color.WHITE)
        // ✅ 아래 줄을 반드시 삭제 또는 주석 처리
        // canvas.drawText("123", 10f, coord.height / 2f, paint)

        return bitmap
    }

    fun captureDummy(): Bitmap? = null
}
