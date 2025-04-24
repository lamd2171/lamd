package com.example.bitconintauto.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.nio.ByteBuffer
import android.graphics.PixelFormat

object ScreenCaptureHelper {
    private var mediaProjection: MediaProjection? = null

    fun setMediaProjection(projection: MediaProjection) {
        mediaProjection = projection
        Log.d("ScreenCaptureHelper", "✅ MediaProjection 객체 저장됨")
    }

    fun getMediaProjection(): MediaProjection? = mediaProjection

    fun captureScreen(
        context: Context,
        projection: MediaProjection,
        cropRatioTop: Float = 0f,
        cropRatioBottom: Float = 1f
    ): Bitmap? {
        return try {
            val metrics = context.resources.displayMetrics
            val scaleFactor = 1
            val width = metrics.widthPixels * scaleFactor
            val height = metrics.heightPixels * scaleFactor
            val dpi = metrics.densityDpi

            val imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)

            projection.registerCallback(object : MediaProjection.Callback() {
                override fun onStop() {
                    Log.d("ScreenCaptureHelper", "🛑 MediaProjection 중단됨")
                }
            }, Handler(Looper.getMainLooper()))

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

            Thread.sleep(1000)
            val image = imageReader.acquireNextImage() ?: run {
                Log.e("ScreenCaptureHelper", "❌ 이미지 획득 실패 (acquireNextImage)")
                virtualDisplay.release()
                return null
            }

            val planes = image.planes
            val buffer: ByteBuffer = planes[0].buffer
            val pixelStride = planes[0].pixelStride
            val rowStride = planes[0].rowStride
            val rowPadding = rowStride - pixelStride * width

            val fullBitmap = Bitmap.createBitmap(
                width + rowPadding / pixelStride,
                height,
                Bitmap.Config.ARGB_8888
            )
            fullBitmap.copyPixelsFromBuffer(buffer)

            image.close()
            virtualDisplay.release()

            val top = (fullBitmap.height * cropRatioTop).toInt()
            val bottom = (fullBitmap.height * cropRatioBottom).toInt()
            Bitmap.createBitmap(fullBitmap, 0, top, fullBitmap.width, bottom - top)

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
