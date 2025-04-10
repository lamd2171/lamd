package com.example.bitconintauto.core

import android.graphics.Bitmap
import com.googlecode.tesseract.android.TessBaseAPI

class OcrProcessor(private val tessBaseAPI: TessBaseAPI) {

    fun recognizeText(bitmap: Bitmap): String {
        tessBaseAPI.setImage(bitmap)
        return tessBaseAPI.utF8Text ?: ""
    }

    fun getNumericValue(bitmap: Bitmap): String? {
        val text = recognizeText(bitmap)
        return Regex("""\d+(\.\d+)?""").find(text)?.value
    }
}
