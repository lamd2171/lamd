// 경로: app/src/main/java/com/example/bitconintauto/util/Utils.kt

package com.example.bitconintauto.util

import android.graphics.Bitmap
import android.graphics.Rect

object Utils {

    fun cropBitmap(bitmap: Bitmap, x: Int, y: Int, width: Int, height: Int): Bitmap {
        val safeX = x.coerceAtLeast(0)
        val safeY = y.coerceAtLeast(0)
        val safeWidth = if (safeX + width <= bitmap.width) width else bitmap.width - safeX
        val safeHeight = if (safeY + height <= bitmap.height) height else bitmap.height - safeY
        return Bitmap.createBitmap(bitmap, safeX, safeY, safeWidth, safeHeight)
    }

    fun getTextRegionRect(x: Int, y: Int): Rect {
        return Rect(x - 50, y - 30, x + 50, y + 30)
    }
}
