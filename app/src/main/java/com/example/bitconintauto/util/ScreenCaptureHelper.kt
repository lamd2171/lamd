package com.example.bitconintauto.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.graphics.Rect
import android.hardware.display.DisplayManager
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.media.Image

object ScreenCaptureHelper {

    private var screenDensity = 1
    private var screenWidth = 1
    private var screenHeight = 1

    fun initialize(context: Context) {
        val metrics = DisplayMetrics()
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getRealMetrics(metrics)
        screenDensity = metrics.densityDpi
        screenWidth = metrics.widthPixels
        screenHeight = metrics.heightPixels
        Log.d("ScreenCaptureHelper", "📱 초기화 완료: $screenWidth x $screenHeight ($screenDensity dpi)")
    }

    fun captureScreen(context: Context, captureRect: Rect): Bitmap? {
        val projection = PermissionUtils.getMediaProjection()
        if (projection == null) {
            Log.e("ScreenCaptureHelper", "❌ MediaProjection이 null임")
            return null
        }

        // 캡처 크기 보정
        val width = captureRect.width().coerceAtLeast(100)
        val height = captureRect.height().coerceAtLeast(80)

        val imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)

        val virtualDisplay = projection.createVirtualDisplay(
            "ScreenCapture",
            width,
            height,
            context.resources.displayMetrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY or DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
            imageReader.surface,
            null,
            null
        )

        Log.d("ScreenCaptureHelper", "🟡 virtualDisplay 생성 완료: $width x $height")

        // 🎯 안정화 시간 확보
        Thread.sleep(500)

        var image: Image? = null
        var attempt = 0

        while (attempt < 6 && image == null) {
            image = imageReader.acquireLatestImage()
            if (image == null) {
                Log.w("ScreenCaptureHelper", "⚠️ 이미지가 null, 재시도 중 ($attempt)")
                Thread.sleep(300)
                attempt++
            }
        }

        if (image != null) {
            val planes = image.planes
            if (planes.isNotEmpty()) {
                val buffer = planes[0].buffer
                val pixelStride = planes[0].pixelStride
                val rowStride = planes[0].rowStride
                val rowPadding = maxOf(0, rowStride - pixelStride * width)
                val extraWidth = if (pixelStride != 0) rowPadding / pixelStride else 0

                val bitmap = Bitmap.createBitmap(
                    width + extraWidth,
                    height,
                    Bitmap.Config.ARGB_8888
                )
                bitmap.copyPixelsFromBuffer(buffer)
                image.close()

                Log.d("ScreenCaptureHelper", "✅ 이미지 복사 완료: ${bitmap.width}x${bitmap.height}")
                return bitmap
            } else {
                Log.e("ScreenCaptureHelper", "❌ planes 비어있음")
            }
            image.close()
        } else {
            Log.e("ScreenCaptureHelper", "❌ 이미지 획득 실패: image == null")
        }

        return null
    }
}
