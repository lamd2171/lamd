package com.example.bitconintauto.util

import android.graphics.Bitmap
import android.graphics.Rect
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.view.PixelCopy
import android.view.SurfaceView
import android.view.Window
import android.view.WindowManager
import androidx.core.graphics.createBitmap

object OCRCaptureUtils {

    private lateinit var window: Window
    private lateinit var surfaceView: SurfaceView

    fun init(window: Window, surfaceView: SurfaceView) {
        this.window = window
        this.surfaceView = surfaceView
    }

    fun captureRegion(region: Rect): Bitmap? {
        if (!::surfaceView.isInitialized || !::window.isInitialized) return null

        val fullBitmap = createBitmap(surfaceView.width, surfaceView.height)
        val latch = Object()
        var captured: Bitmap? = null

        val handlerThread = HandlerThread("PixelCopyThread")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)

        PixelCopy.request(surfaceView, fullBitmap, { result ->
            if (result == PixelCopy.SUCCESS) {
                try {
                    captured = Bitmap.createBitmap(
                        fullBitmap,
                        region.left,
                        region.top,
                        region.width(),
                        region.height()
                    )
                } catch (_: Exception) {
                }
            }
            synchronized(latch) { latch.notify() }
        }, handler)

        synchronized(latch) { latch.wait(500) }
        handlerThread.quitSafely()
        return captured
    }
}