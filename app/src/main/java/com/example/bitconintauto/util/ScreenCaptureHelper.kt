package com.example.bitconintauto.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.util.DisplayMetrics
import android.util.Log
import java.nio.ByteBuffer

object ScreenCaptureHelper {
    private var mediaProjection: MediaProjection? = null

    fun setMediaProjection(projection: MediaProjection) {
        mediaProjection = projection
        Log.d("ScreenCaptureHelper", "✅ MediaProjection 객체 저장됨")
    }

    fun getMediaProjection(): MediaProjection? = mediaProjection

    fun captureScreen(context: Context, projection: MediaProjection): Bitmap? {
        return try {
            val metrics = context.resources.displayMetrics
            val width = 1080
            val height = 2400
            val dpi = metrics.densityDpi

            val imageReader = ImageReader.newInstance(width, height, ImageFormat.RGB_565, 2)
            val virtualDisplay: VirtualDisplay = projection.createVirtualDisplay(
                "ScreenCapture",
                width,
                height,
                dpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader.surface,
                null,
                null
            )

            Thread.sleep(300)
            val image = imageReader.acquireLatestImage() ?: run {
                Log.e("ScreenCaptureHelper", "❌ 이미지 획득 실패")
                virtualDisplay.release()
                return null
            }

            val planes = image.planes
            val buffer: ByteBuffer = planes[0].buffer
            val pixelStride = planes[0].pixelStride
            val rowStride = planes[0].rowStride
            val rowPadding = rowStride - pixelStride * width

            val bitmap = Bitmap.createBitmap(
                width + rowPadding / pixelStride,
                height,
                Bitmap.Config.RGB_565
            )
            bitmap.copyPixelsFromBuffer(buffer)

            image.close()
            virtualDisplay.release()

            bitmap
        } catch (e: Exception) {
            Log.e("ScreenCaptureHelper", "❌ 예외 발생: ${e.message}")
            null
        }
    }

    fun stopProjection() {
        mediaProjection?.stop()
        mediaProjection = null
        Log.d("ScreenCaptureHelper", "❌ MediaProjection stopped")
    }
}
