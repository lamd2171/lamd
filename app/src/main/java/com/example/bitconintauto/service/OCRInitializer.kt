package com.example.bitconintauto.service

import android.content.Context
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object OCRInitializer {
    fun initializeTesseract(context: Context): TessBaseAPI {
        val tessDataPath = "${context.filesDir}/tessdata"
        val tessDataDir = File(tessDataPath)
        if (!tessDataDir.exists()) {
            tessDataDir.mkdirs()
        }

        val trainedDataFile = File(tessDataDir, "eng.traineddata")
        if (!trainedDataFile.exists()) {
            val inputStream: InputStream = context.assets.open("tessdata/eng.traineddata")
            val outputStream = FileOutputStream(trainedDataFile)
            val buffer = ByteArray(1024)
            var read: Int
            while (inputStream.read(buffer).also { read = it } != -1) {
                outputStream.write(buffer, 0, read)
            }
            inputStream.close()
            outputStream.close()
        }

        return TessBaseAPI().apply {
            init(context.filesDir.absolutePath, "eng")
        }
    }
}
