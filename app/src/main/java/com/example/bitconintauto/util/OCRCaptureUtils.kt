package com.example.bitconintauto.util

import android.graphics.Bitmap
import android.graphics.Rect
import com.example.bitconintauto.model.Coordinate

object OCRCaptureUtils {
    fun captureRegion(bitmap: Bitmap, coord: Coordinate): Bitmap? {
        val rect = Rect(coord.x, coord.y, coord.x + coord.width, coord.y + coord.height)
        return try {
            Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height())
        } catch (e: Exception) {
            null
        }
    }
}
