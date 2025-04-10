package com.example.bitconintauto.util

import android.accessibilityservice.AccessibilityService
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.graphics.Rect
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.os.Handler
import android.view.Surface
import java.nio.ByteBuffer

object OCRCaptureUtils {

    fun captureArea(projection: MediaProjection, bounds: Rect, onCaptured: (Bitmap?) -> Unit) {
        val reader = ImageReader.newInstance(bounds.width(), bounds.height(), PixelFormat.RGBA_8888, 2)
        val surface: Surface = reader.surface
        val virtualDisplay = projection.createVirtualDisplay(
            "capture", bounds.width(), bounds.height(), 1,
            0, surface, null, null
        )

        Handler().postDelayed({
            val image = reader.acquireLatestImage()
            if (image != null) {
                val plane = image.planes[0]
                val buffer: ByteBuffer = plane.buffer
                val pixelStride = plane.pixelStride
                val rowStride = plane.rowStride
                val rowPadding = rowStride - pixelStride * bounds.width()
                val bitmap = Bitmap.createBitmap(
                    bounds.width() + rowPadding / pixelStride,
                    bounds.height(), Bitmap.Config.ARGB_8888
                )
                bitmap.copyPixelsFromBuffer(buffer)
                image.close()
                reader.close()
                virtualDisplay.release()
                onCaptured(bitmap)
            } else {
                onCaptured(null)
            }
        }, 300)
    }

    fun recognizeText(bitmap: Bitmap, ocr: OCRProcessor): String {
        return ocr.getText(bitmap)
    }
}
