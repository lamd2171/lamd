package com.example.bitconintauto.util

import android.content.Context
import android.graphics.Bitmap
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface

object ScreenCaptureHelper {

    private var mediaProjection: MediaProjection? = null
    private lateinit var context: Context
    private var imageReader: ImageReader? = null
    private var handler: Handler? = null

    fun init(projection: MediaProjection, ctx: Context) {
        mediaProjection = projection
        context = ctx

        val width = 540
        val height = 960
        val dpi = 240

        imageReader = ImageReader.newInstance(width, height, 0x1, 2)
        val handlerThread = HandlerThread("ScreenCaptureThread").apply { start() }
        handler = Handler(handlerThread.looper)

        mediaProjection?.createVirtualDisplay(
            "CaptureDisplay",
            width, height, dpi,
            0, imageReader!!.surface,
            null, handler
        )
    }

    fun capture(callback: (Bitmap?) -> Unit) {
        val image = imageReader?.acquireLatestImage() ?: return callback(null)

        val planes = image.planes
        val buffer = planes[0].buffer
        val pixelStride = planes[0].pixelStride
        val rowStride = planes[0].rowStride
        val rowPadding = rowStride - pixelStride * image.width

        val bitmap = Bitmap.createBitmap(
            image.width + rowPadding / pixelStride,
            image.height, Bitmap.Config.ARGB_8888
        )
        bitmap.copyPixelsFromBuffer(buffer)
        image.close()

        callback(bitmap)
    }
}
