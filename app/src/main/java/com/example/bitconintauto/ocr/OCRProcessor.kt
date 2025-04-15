// [3] app/src/main/java/com/example/bitconintauto/ocr/OCRProcessor.kt

package com.example.bitconintauto.ocr

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.googlecode.tesseract.android.TessBaseAPI

class OCRProcessor {
    private var tessBaseAPI: TessBaseAPI? = null

    fun init(context: Context) {
        tessBaseAPI = TessBaseAPI().apply {
            init("/sdcard/tesseract/", "eng")
            setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_BLOCK)
        }
    }

    fun getText(bitmap: Bitmap): String {
        return try {
            tessBaseAPI?.setImage(bitmap)
            tessBaseAPI?.utF8Text ?: ""
        } catch (e: Exception) {
            Log.e("OCRProcessor", "OCR 실패: ${e.message}")
            ""
        }
    }

    fun release() {
        tessBaseAPI?.end()
    }
}