package com.example.bitconintauto.ocr

import android.content.Context
import android.graphics.Bitmap
import com.googlecode.tesseract.android.TessBaseAPI

class OCRProcessor {

    private var tessBaseAPI: TessBaseAPI? = null

    fun init(context: Context) {
        val path = "${context.filesDir}/tesseract/"
        tessBaseAPI = TessBaseAPI()
        tessBaseAPI?.init(path, "eng")
    }

    fun getText(bitmap: Bitmap): String {
        tessBaseAPI?.setImage(bitmap)
        return tessBaseAPI?.utF8Text ?: ""
    }
}
