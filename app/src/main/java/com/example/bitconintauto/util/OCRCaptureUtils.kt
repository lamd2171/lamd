package com.example.bitconintauto.util

import android.accessibilityservice.AccessibilityService
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.example.bitconintauto.model.Coordinate
import java.nio.ByteBuffer

object OCRCaptureUtils {

    private var projection: MediaProjection? = null
    private var imageReader: ImageReader? = null

    fun setMediaProjection(mediaProjection: MediaProjection) {
        projection = mediaProjection
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun capture(service: AccessibilityService, coord: Coordinate): Bitmap? {
        val windowManager = service.getSystemService(WindowManager::class.java)
        val metrics = DisplayMetrics().also {
            windowManager.defaultDisplay.getRealMetrics(it)
        }

        val screenWidth = metrics.widthPixels
        val screenHeight = metrics.heightPixels
        val screenDensity = metrics.densityDpi

        if (imageReader == null) {
            imageReader = ImageReader.newInstance(screenWidth, screenHeight, 0x1, 2)
        }

        projection?.createVirtualDisplay(
            "ScreenCapture",
            screenWidth,
            screenHeight,
            screenDensity,
            0,
            imageReader?.surface,
            null,
            null
        ) ?: return null

        val image = imageReader?.acquireLatestImage() ?: return null

        val planes = image.planes
        val buffer: ByteBuffer = planes[0].buffer
        val pixelStride = planes[0].pixelStride
        val rowStride = planes[0].rowStride
        val rowPadding = rowStride - pixelStride * screenWidth

        val bitmap = Bitmap.createBitmap(
            screenWidth + rowPadding / pixelStride,
            screenHeight,
            Bitmap.Config.ARGB_8888
        )
        bitmap.copyPixelsFromBuffer(buffer)
        image.close()

        val rect = Rect(coord.x, coord.y, coord.x + coord.width, coord.y + coord.height)
        return Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height())
    }

    fun captureDummy(): Bitmap? = null
}