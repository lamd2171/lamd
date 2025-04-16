// app/src/main/java/com/example/bitconintauto/util/ScreenCaptureHelper.kt
package com.example.bitconintauto.util

import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.util.DisplayMetrics
import android.view.WindowManager
import com.example.bitconintauto.BitconintAutoApp
import android.graphics.PixelFormat

object ScreenCaptureHelper {

    private var mediaProjection: MediaProjection? = null
    private var imageReader: ImageReader? = null
    private var virtualDisplay: VirtualDisplay? = null

    fun setUpProjection() {
        val appContext = BitconintAutoApp.context
        val projection = PermissionUtils.getMediaProjection(appContext) ?: return
        setMediaProjection(projection)
    }

    fun setMediaProjection(projection: MediaProjection) {
        mediaProjection = projection

        val metrics = DisplayMetrics()
        val windowManager = BitconintAutoApp.context.getSystemService(WindowManager::class.java)
        windowManager?.defaultDisplay?.getRealMetrics(metrics)

        imageReader = ImageReader.newInstance(
            metrics.widthPixels,
            metrics.heightPixels,
            PixelFormat.RGBA_8888, // ← 더 넓은 기기 호환성 확보
            2
        )

        virtualDisplay = mediaProjection?.createVirtualDisplay(
            "ScreenCapture",
            metrics.widthPixels, metrics.heightPixels, metrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader?.surface, null, null
        )
    }

    fun captureSync(): Bitmap? {
        val image = imageReader?.acquireLatestImage() ?: return null

        return try {
            val width = image.width
            val height = image.height

            val plane = image.planes[0]
            val buffer = plane.buffer
            val pixelStride = plane.pixelStride
            val rowStride = plane.rowStride
            val rowPadding = rowStride - pixelStride * width

            val tempBitmap = Bitmap.createBitmap(
                width + rowPadding / pixelStride,
                height,
                Bitmap.Config.ARGB_8888
            )
            tempBitmap.copyPixelsFromBuffer(buffer)

            val result = Bitmap.createBitmap(tempBitmap, 0, 0, width, height)
            image.close()
            result
        } catch (e: Exception) {
            image.close()
            e.printStackTrace()
            null
        }
    }

}
