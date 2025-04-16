package com.example.bitconintauto.ocr

import android.graphics.Bitmap
import android.util.Log
import com.googlecode.tesseract.android.TessBaseAPI

class OCRProcessor {

    private val tessBaseAPI: TessBaseAPI by lazy {
        TessBaseAPI().apply {
            init("/sdcard/tesseract/", "eng")
            setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_BLOCK)
        }
    }

    fun getText(bitmap: Bitmap): String {
        return try {
            tessBaseAPI.setImage(bitmap)
            val result = tessBaseAPI.utF8Text ?: ""
            Log.d("OCR", "결과 → $result")
            result
        } catch (e: Exception) {
            ""
        }
    }
}
