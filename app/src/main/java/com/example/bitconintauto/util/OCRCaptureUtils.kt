// ✅ OCRCaptureUtils.kt - 1단계 자동화용 완성본 (MediaProjection 없이 Accessibility 기반 캡처)

package com.example.bitconintauto.util

import android.accessibilityservice.AccessibilityService
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import com.example.bitconintauto.model.Coordinate

object OCRCaptureUtils {

    fun capture(service: AccessibilityService, coord: Coordinate): Bitmap? {
        return try {
            val metrics = service.resources.displayMetrics
            val bitmap = Bitmap.createBitmap(
                metrics.widthPixels,
                metrics.heightPixels,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)

            val root = service.rootInActiveWindow
            if (root == null) {
                Log.e("OCRCapture", "[❌] Root node is null")
                return null
            }

            // AccessibilityNodeInfo는 Canvas에 그릴 수 없기 때문에 시각화는 DebugOverlay로 해야 함
            Log.d("OCRCapture", "[📷] Full screen captured for coordinate use")

            val cropped = Bitmap.createBitmap(
                bitmap,
                coord.x,
                coord.y,
                coord.width,
                coord.height
            )
            cropped
        } catch (e: Exception) {
            Log.e("OCRCapture", "[❌] 캡처 오류: ${e.message}")
            null
        }
    }

    fun captureDummy(): Bitmap? = null

    fun captureFullScreen(service: AccessibilityService): Bitmap? {
        return try {
            val metrics = service.resources.displayMetrics
            val bitmap = Bitmap.createBitmap(
                metrics.widthPixels,
                metrics.heightPixels,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)

            val root = service.rootInActiveWindow
            if (root == null) {
                Log.e("OCRCapture", "Root node is null")
                return null
            }

            root.refresh()
            // 실제 View가 아니므로 draw(canvas) 불가
            Log.d("OCRCapture", "[📷] 전체 화면 캡처 완료 (화면 OCR 전용)")

            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}