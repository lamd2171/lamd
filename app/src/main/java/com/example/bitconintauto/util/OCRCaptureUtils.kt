package com.example.bitconintauto.util

import android.graphics.Bitmap
import android.graphics.Rect
import com.example.bitconintauto.model.Coordinate

object OCRCaptureUtils {
    fun captureRegion(bitmap: Bitmap, coord: Coordinate): Bitmap {
        val x = coord.x
        val y = coord.y
        val width = coord.width
        val height = coord.height

        return Bitmap.createBitmap(bitmap, x, y, width, height)
    }
}
