package com.example.bitconintauto.util

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.PixelFormat
import android.graphics.Rect
import android.hardware.display.DisplayManager
import android.media.ImageReader
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.media.projection.MediaProjectionManager
import android.media.projection.MediaProjection

object ScreenCaptureHelper {

    private var mediaProjection: MediaProjection? = null
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

    fun setMediaProjection(projection: MediaProjection) {
        mediaProjection = projection
    }

    fun captureScreen(context: Context, rect: Rect): Bitmap? {
        if (mediaProjection == null) {
            Log.e("ScreenCaptureHelper", "❌ MediaProjection이 null임")
            return null
        }

        val width = rect.width()
        val height = rect.height()
        val imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)

        val virtualDisplay = mediaProjection?.createVirtualDisplay(
            "ScreenCapture",
            width,
            height,
            screenDensity,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader.surface,
            null,
            null
        )

        Thread.sleep(200) // 캡처 안정화 지연

        val image = imageReader.acquireLatestImage()
        if (image != null) {
            val planes = image.planes
            val buffer = planes[0].buffer
            val pixelStride = planes[0].pixelStride
            val rowStride = planes[0].rowStride
            val rowPadding = rowStride - pixelStride * width

            val bitmap = Bitmap.createBitmap(
                width + rowPadding / pixelStride,
                height,
                Bitmap.Config.ARGB_8888
            )
            bitmap.copyPixelsFromBuffer(buffer)
            image.close()
            imageReader.close()
            virtualDisplay?.release()
            return Bitmap.createBitmap(bitmap, 0, 0, width, height)
        } else {
            Log.e("ScreenCaptureHelper", "❌ 캡처 실패: image == null")
        }

        imageReader.close()
        virtualDisplay?.release()
        return null
    }
}
