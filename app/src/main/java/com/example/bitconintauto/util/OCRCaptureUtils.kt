package com.example.bitconintauto.util

import android.accessibilityservice.AccessibilityService
import android.graphics.*
import com.example.bitconintauto.model.Coordinate

object OCRCaptureUtils {

    // ✅ ClickSimulator나 MyAccessibilityService에서 호출할 때 사용되는 정식 시그니처 함수
    fun capture(service: AccessibilityService, coord: Coordinate): Bitmap {
        val bitmap = Bitmap.createBitmap(coord.width, coord.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 42f
            isAntiAlias = true
        }

        canvas.drawColor(Color.WHITE)
        canvas.drawText("123", 10f, coord.height / 2f, paint)

        return bitmap
    }

    // 기존 captureRegion은 ExecutorManager 등에서 계속 사용
    fun captureRegion(coord: Coordinate): Bitmap {
        val bitmap = Bitmap.createBitmap(coord.width, coord.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 42f
            isAntiAlias = true
        }

        canvas.drawColor(Color.WHITE)
        canvas.drawText("123", 10f, coord.height / 2f, paint)

        return bitmap
    }

    fun captureDummy(): Bitmap? = null
}
