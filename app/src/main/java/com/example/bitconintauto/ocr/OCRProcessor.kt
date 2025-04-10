package com.example.bitconintauto.ocr

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

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
                context.assets.open("tessdata/eng.traineddata").use { inputStream ->
                    FileOutputStream(trainedDataFile).use { outputStream ->
                        val buffer = ByteArray(1024)
                        var read: Int
                        while (inputStream.read(buffer).also { read = it } != -1) {
                            outputStream.write(buffer, 0, read)
                        }
                        outputStream.flush()
                    }
                }
            } catch (e: Exception) {
                Log.e("OCRProcessor", "Failed to copy traineddata: ${e.message}")
            }
        }

        val dataPath = File(context.filesDir, "tesseract").absolutePath
        val initSuccess = tessBaseApi.init(dataPath, "eng")
        if (!initSuccess) {
            Log.e("OCRProcessor", "TessBaseAPI initialization failed!")
        }
    }

    fun getText(bitmap: Bitmap): String {
        return try {
            tessBaseApi.setImage(bitmap)
            tessBaseApi.utF8Text ?: ""
        } catch (e: Exception) {
            Log.e("OCRProcessor", "Error during OCR: ${e.message}")
            ""
        }
    }

    fun stop() {
        tessBaseApi.end()
    }
}
