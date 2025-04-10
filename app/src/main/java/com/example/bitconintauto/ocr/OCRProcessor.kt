package com.example.bitconintauto.ocr

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.File
import java.io.FileOutputStream

class OCRProcessor {

    private val tessBaseApi: TessBaseAPI = TessBaseAPI()

    fun init(context: Context) {
        val tessDir = File(context.filesDir, "tesseract/tessdata")
        if (!tessDir.exists()) {
            tessDir.mkdirs()
        }

        val trainedDataFile = File(tessDir, "eng.traineddata")
        if (!trainedDataFile.exists()) {
            try {
                context.assets.open("tessdata/eng.traineddata").use { input ->
                    FileOutputStream(trainedDataFile).use { output ->
                        val buffer = ByteArray(1024)
                        var read: Int
                        while (input.read(buffer).also { read = it } != -1) {
                            output.write(buffer, 0, read)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("OCRProcessor", "traineddata 복사 실패: ${e.message}")
            }
        }

        val dataPath = File(context.filesDir, "tesseract").absolutePath
        val initSuccess = tessBaseApi.init(dataPath, "eng")

        if (!initSuccess) {
            Log.e("OCRProcessor", "TessBaseAPI 초기화 실패!")
        }
    }

    fun getText(bitmap: Bitmap): String {
        return try {
            tessBaseApi.setImage(bitmap)
            tessBaseApi.utF8Text ?: ""
        } catch (e: Exception) {
            Log.e("OCRProcessor", "OCR 처리 중 오류: ${e.message}")
            ""
        }
    }

    fun stop() {
        tessBaseApi.end()
    }
}
