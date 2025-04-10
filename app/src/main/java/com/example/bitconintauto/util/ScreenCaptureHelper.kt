package com.example.bitconintauto.util

import android.graphics.Bitmap
import android.graphics.Rect
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.os.Handler
import android.view.PixelCopy
import android.view.SurfaceView
import android.view.Window
import androidx.core.graphics.createBitmap

object ScreenCaptureHelper {

    fun captureRegion(view: SurfaceView, region: Rect, onCaptured: (Bitmap?) -> Unit) {
        val bitmap = createBitmap(view.width, view.height)
        PixelCopy.request(view, bitmap, { copyResult ->
            if (copyResult == PixelCopy.SUCCESS) {
                val cropped = Bitmap.createBitmap(
                    bitmap,
                    region.left,
                    region.top,
                    region.width(),
                    region.height()
                )
                onCaptured(cropped)
            } else {
                AppLogger.error("PixelCopy 실패")
                onCaptured(null)
            }
        }, Handler())
    }
}
