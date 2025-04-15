// [2] app/src/main/java/com/example/bitconintauto/util/OCRCaptureUtils.kt

package com.example.bitconintauto.util

import android.graphics.Bitmap
import android.graphics.Rect
import com.example.bitconintauto.model.Coordinate

object OCRCaptureUtils {
    fun captureRegion(bitmap: Bitmap, coord: Coordinate): Bitmap {
        val rect = Rect(coord.x, coord.y, coord.x + coord.width, coord.y + coord.height)
        return Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height())
    }
}