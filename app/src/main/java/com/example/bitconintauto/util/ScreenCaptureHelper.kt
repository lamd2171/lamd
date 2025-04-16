package com.example.bitconintauto.util

import android.graphics.Bitmap
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import java.nio.ByteBuffer
import android.content.Context

object ScreenCaptureHelper {
    private var mediaProjection: MediaProjection? = null
    private var imageReader: ImageReader? = null
    private var virtualDisplay: VirtualDisplay? = null

    fun init(projection: MediaProjection, context: android.content.Context) {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val width = display.width
        val height = display.height

        imageReader = ImageReader.newInstance(width, height, 0x1, 2)
        virtualDisplay = projection.createVirtualDisplay(
            "ScreenCapture",
            width, height, context.resources.displayMetrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader?.surface, null, null
        )
        mediaProjection = projection
    }

    fun capture(callback: (Bitmap?) -> Unit) {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            val image = imageReader?.acquireLatestImage()
            if (image != null) {
                val width = image.width
                val height = image.height
                val plane = image.planes[0]
                val buffer: ByteBuffer = plane.buffer
                val pixelStride: Int = plane.pixelStride
                val rowStride: Int = plane.rowStride
                val rowPadding = rowStride - pixelStride * width
                val bitmap = Bitmap.createBitmap(
                    width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888
                )
                bitmap.copyPixelsFromBuffer(buffer)
                image.close()
                callback(bitmap)
            } else {
                callback(null)
            }
        }, 300)
    }

    // captureSync() 추가: 화면 캡처를 동기적으로 처리하는 메서드
    fun captureSync(): Bitmap? {
        val image = imageReader?.acquireLatestImage()
        image?.let {
            val width = it.width
            val height = it.height
            val plane = it.planes[0]
            val buffer: ByteBuffer = plane.buffer
            val pixelStride: Int = plane.pixelStride
            val rowStride: Int = plane.rowStride
            val rowPadding = rowStride - pixelStride * width
            val bitmap = Bitmap.createBitmap(
                width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888
            )
            bitmap.copyPixelsFromBuffer(buffer)
            it.close()
            return bitmap
        }
        return null
    }
}
