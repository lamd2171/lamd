package com.example.bitconintauto.ocr

import android.content.Context
import android.graphics.Bitmap
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.File
import java.io.FileOutputStream

class OCRProcessor {

    private val tessBaseApi: TessBaseAPI = TessBaseAPI()

    fun init(context: Context) {
        val tessDir = File(context.filesDir, "tesseract/tessdata")
        if (!tessDir.exists()) tessDir.mkdirs()

        val trainedData = File(tessDir, "eng.traineddata")
        if (!trainedData.exists()) {
            context.assets.open("tessdata/eng.traineddata").use { input ->
                FileOutputStream(trainedData).use { output ->
                    input.copyTo(output)
                }
            }
        }

        tessBaseApi.init(tessDir.parent, "eng")
    }

    fun getText(bitmap: Bitmap): String {
        tessBaseApi.setImage(bitmap)
        return tessBaseApi.utF8Text ?: ""
    }

    fun release() {
        tessBaseApi.end()
    }
}
