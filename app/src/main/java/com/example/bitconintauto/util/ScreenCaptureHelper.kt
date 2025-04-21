package com.example.bitconintauto.util

import android.content.Context
import android.graphics.Bitmap
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager

object ScreenCaptureHelper {

    private var mediaProjection: MediaProjection? = null

    fun setMediaProjection(projection: MediaProjection) {
        mediaProjection = projection
    }

    fun getMediaProjection(): MediaProjection? {
        return mediaProjection
    }

    // ✅ 새 구조: MediaProjection을 외부에서 직접 전달
    fun captureScreen(context: Context, projection: MediaProjection): Bitmap? {
        return try {
            capture(context, projection)
        } catch (e: Exception) {
            Log.e("ScreenCaptureHelper", "❌ 예외 발생: ${e.message}")
            null
        }
    }

    // ✅ 기존 구조: 내부 static projection 사용
    fun captureScreen(context: Context): Bitmap? {
        val projection = mediaProjection
        if (projection == null) {
            Log.e("ScreenCaptureHelper", "❌ MediaProjection is null")
            return null
        }
        return capture(context, projection)
    }

    // ✅ 공통 캡처 로직
    private fun capture(context: Context, projection: MediaProjection): Bitmap? {
        val width: Int
        val height: Int
        val density: Int

        val displayMetrics = context.resources.displayMetrics
        density = displayMetrics.densityDpi

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val windowMetrics = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).currentWindowMetrics
            val bounds = windowMetrics.bounds
            width = bounds.width()
            height = bounds.height()
        } else {
            val metrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getRealMetrics(metrics)
            width = metrics.widthPixels
            height = metrics.heightPixels
        }

        val imageReader = ImageReader.newInstance(width, height, 0x1, 2)

        val virtualDisplay: VirtualDisplay = projection.createVirtualDisplay(
            "ScreenCapture",
            width,
            height,
            density,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader.surface,
            null,
            Handler(Looper.getMainLooper())
        )

        Thread.sleep(300) // 안정성 확보

        val image = imageReader.acquireLatestImage()
        if (image == null) {
            Log.e("ScreenCaptureHelper", "❌ 이미지 획득 실패 (null)")
            virtualDisplay.release()
            return null
        }

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
        virtualDisplay.release()

        return Bitmap.createBitmap(bitmap, 0, 0, width, height)
    }
}
