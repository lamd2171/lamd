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

    // ✅ 여기 추가!
    fun getMediaProjection(): MediaProjection? {
        return PermissionUtils.getMediaProjection()
    }

    fun captureScreen(context: Context, captureRect: Rect): Bitmap? {
        val projection =  mediaProjection
        if (projection == null) {
            Log.e("ScreenCaptureHelper", "❌ MediaProjection이 null임")
            return null
        }

        val imageReader = ImageReader.newInstance(captureRect.width(), captureRect.height(), PixelFormat.RGBA_8888, 2)

        val virtualDisplay = projection.createVirtualDisplay(
            "ScreenCapture",
            captureRect.width(),
            captureRect.height(),
            context.resources.displayMetrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY or DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
            imageReader.surface,
            null,
            null
        )

        Thread.sleep(300) // 캡처 안정성 확보용

        val image = imageReader.acquireLatestImage()
        if (image != null) {
            val planes = image.planes
            val buffer = planes[0].buffer
            val pixelStride = planes[0].pixelStride
            val rowStride = planes[0].rowStride
            val rowPadding = rowStride - pixelStride * captureRect.width()
            val bitmap = Bitmap.createBitmap(
                captureRect.width() + rowPadding / pixelStride,
                captureRect.height(),
                Bitmap.Config.ARGB_8888
            )
            bitmap.copyPixelsFromBuffer(buffer)
            image.close()
            imageReader.close()
            virtualDisplay.release()
            return Bitmap.createBitmap(bitmap, 0, 0, captureRect.width(), captureRect.height())
        }

        Log.e("ScreenCaptureHelper", "❌ 캡처 실패: bitmap == null")
        imageReader.close()
        virtualDisplay.release()
        return null
    }

}
