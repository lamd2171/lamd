package com.example.bitconintauto.util

import android.graphics.Bitmap
import com.example.bitconintauto.model.Coordinate

object OCRCaptureUtils {
    fun captureRegion(bitmap: Bitmap, coord: Coordinate): Bitmap {
        val x = coord.x
        val y = coord.y
        val width = coord.width
        val height = coord.height

        val safeWidth = if (x + width > bitmap.width) bitmap.width - x else width
        val safeHeight = if (y + height > bitmap.height) bitmap.height - y else height

        return Bitmap.createBitmap(bitmap, x, y, safeWidth, safeHeight)
    }
}
