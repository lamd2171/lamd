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
    fun captureRegion(region: Rect): Bitmap? {
        val bitmap = Bitmap.createBitmap(region.width(), region.height(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE) // 테스트용 흰 배경
        return bitmap
    }
}