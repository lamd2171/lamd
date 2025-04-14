// 📄 파일 경로: app/src/main/java/com/example/bitconintauto/util/ScreenCaptureHelper.kt

package com.example.bitconintauto.util

import android.graphics.Bitmap
import android.graphics.Rect
import android.view.PixelCopy
import android.view.SurfaceView
import android.os.Handler
import android.util.Log
import androidx.core.graphics.createBitmap

object ScreenCaptureHelper {
    fun captureRegion(view: SurfaceView, region: Rect, onCaptured: (Bitmap?) -> Unit) {
        val bitmap = createBitmap(view.width, view.height)
        PixelCopy.request(view, bitmap, { result ->
            if (result == PixelCopy.SUCCESS) {
                try {
                    val cropped = Bitmap.createBitmap(
                        bitmap,
                        region.left,
                        region.top,
                        region.width(),
                        region.height()
                    )
                    onCaptured(cropped)
                } catch (e: Exception) {
                    Log.e("ScreenCaptureHelper", "잘못된 영역 캡처", e)
                    onCaptured(null)
                }
            } else {
                Log.e("ScreenCaptureHelper", "PixelCopy 실패: $result")
                onCaptured(null)
            }
        }, Handler())
    }
}