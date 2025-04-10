package com.example.bitconintauto.service

import android.graphics.Bitmap
import android.graphics.Rect
import com.googlecode.tesseract.android.TessBaseAPI

class OCRProcessor(private val tessBaseAPI: TessBaseAPI) {

    fun recognizeNumber(bitmap: Bitmap): Int? {
        tessBaseAPI.setImage(bitmap)
        val text = tessBaseAPI.utF8Text ?: return null
        val match = Regex("\\d+").find(text)
        return match?.value?.toIntOrNull()
    }

    fun recognizeFloat(bitmap: Bitmap): Float? {
        tessBaseAPI.setImage(bitmap)
        val text = tessBaseAPI.utF8Text ?: return null
        val match = Regex("\\d+(\\.\\d+)?").find(text)
        return match?.value?.toFloatOrNull()
    }

    fun getTextInRegion(bitmap: Bitmap, region: Rect): String {
        val cropped = Bitmap.createBitmap(bitmap, region.left, region.top, region.width(), region.height())
        tessBaseAPI.setImage(cropped)
        return tessBaseAPI.utF8Text ?: ""
    }
}
