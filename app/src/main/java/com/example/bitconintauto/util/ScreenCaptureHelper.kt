// [1] app/src/main/java/com/example/bitconintauto/util/ScreenCaptureHelper.kt

package com.example.bitconintauto.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.Surface

object ScreenCaptureHelper {
    private const val VIRTUAL_DISPLAY_NAME = "ScreenCapture"
    private const val VIRTUAL_DISPLAY_FLAGS = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY or
            DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC

    private var mediaProjection: MediaProjection? = null
    private var imageReader: ImageReader? = null
    private var virtualDisplay: VirtualDisplay? = null
    private lateinit var metrics: DisplayMetrics

    fun init(mediaProjection: MediaProjection, context: Context) {
        this.mediaProjection = mediaProjection
        this.metrics = context.resources.displayMetrics

        imageReader = ImageReader.newInstance(
            metrics.widthPixels,
            metrics.heightPixels,
            PixelFormat.RGBA_8888,
            2
        )

        virtualDisplay = mediaProjection.createVirtualDisplay(
            VIRTUAL_DISPLAY_NAME,
            metrics.widthPixels,
            metrics.heightPixels,
            metrics.densityDpi,
            VIRTUAL_DISPLAY_FLAGS,
            imageReader?.surface,
            null,
            Handler(Looper.getMainLooper())
        )
    }

    fun capture(callback: (Bitmap?) -> Unit) {
        val reader = imageReader ?: run {
            callback(null)
            return
        }

        val image = reader.acquireLatestImage() ?: run {
            callback(null)
            return
        }

        val planes = image.planes
        val buffer = planes[0].buffer
        val pixelStride = planes[0].pixelStride
        val rowStride = planes[0].rowStride
        val rowPadding = rowStride - pixelStride * metrics.widthPixels

        val bitmap = Bitmap.createBitmap(
            metrics.widthPixels + rowPadding / pixelStride,
            metrics.heightPixels,
            Bitmap.Config.ARGB_8888
        )
        bitmap.copyPixelsFromBuffer(buffer)
        image.close()

        callback(bitmap)
    }

    fun release() {
        virtualDisplay?.release()
        imageReader?.close()
        mediaProjection?.stop()
    }
}
