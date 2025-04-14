package com.example.bitconintauto.ocr

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.File
import java.io.FileOutputStream

object OCRProcessor {

    private var tessBaseAPI: TessBaseAPI? = null
    private const val TESS_DATA_PATH = "tessdata"
    private const val LANG = "eng"

    fun init(context: Context) {
        val dir = File(context.filesDir, TESS_DATA_PATH)
        if (!dir.exists()) dir.mkdirs()

        val trainedData = File(dir, "$LANG.traineddata")
        if (!trainedData.exists()) {
            context.assets.open("tessdata/$LANG.traineddata").use { input ->
                FileOutputStream(trainedData).use { output ->
                    input.copyTo(output)
                }
            }
        }

        tessBaseAPI = TessBaseAPI().apply {
            init(context.filesDir.absolutePath, LANG)
        }
    }

    fun recognizeText(bitmap: Bitmap): String {
        return try {
            tessBaseAPI?.setImage(bitmap)
            tessBaseAPI?.utF8Text?.trim() ?: ""
        } catch (e: Exception) {
            Log.e("OCRProcessor", "OCR 실패: ${e.message}")
            ""
        }
    }

    fun release() {
        tessBaseAPI?.end()
        tessBaseAPI = null
    }
}
