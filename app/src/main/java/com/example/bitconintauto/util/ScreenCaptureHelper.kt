package com.example.bitconintauto.util

import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.PixelCopy
import android.view.SurfaceView
import androidx.core.graphics.createBitmap

object ScreenCaptureHelper {

    fun captureRegion(view: SurfaceView, region: Rect, onCaptured: (Bitmap?) -> Unit) {
        val viewWidth = view.width
        val viewHeight = view.height

        if (viewWidth == 0 || viewHeight == 0) {
            Log.e("ScreenCaptureHelper", "SurfaceView 크기가 유효하지 않음 (width=0 or height=0)")
            onCaptured(null)
            return
        }

        val fullBitmap = createBitmap(viewWidth, viewHeight)

        try {
            PixelCopy.request(
                view,
                fullBitmap,
                { result ->
                    if (result == PixelCopy.SUCCESS) {
                        // 안전한 좌표 계산
                        val clippedRect = Rect(
                            region.left.coerceIn(0, viewWidth),
                            region.top.coerceIn(0, viewHeight),
                            region.right.coerceIn(0, viewWidth),
                            region.bottom.coerceIn(0, viewHeight)
                        )

                        val cropped = Bitmap.createBitmap(
                            fullBitmap,
                            clippedRect.left,
                            clippedRect.top,
                            clippedRect.width(),
                            clippedRect.height()
                        )

                        onCaptured(cropped)
                    } else {
                        Log.e("ScreenCaptureHelper", "PixelCopy 실패: 코드 $result")
                        onCaptured(null)
                    }
                },
                Handler(Looper.getMainLooper()) // ✅ deprecated 해결
            )
        } catch (e: Exception) {
            Log.e("ScreenCaptureHelper", "PixelCopy 예외 발생", e)
            onCaptured(null)
        }
    }
}
