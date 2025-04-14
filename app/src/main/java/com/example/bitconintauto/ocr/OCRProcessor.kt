package com.example.bitconintauto.ocr

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.File
import java.io.FileOutputStream

class OCRProcessor {

    private var tessBaseAPI: TessBaseAPI? = null

    fun init(context: Context) {
        val basePath = context.filesDir.absolutePath      // /data/data/패키지명/files
        val tessdataDir = File("$basePath/tessdata")      // ✅ /files/tessdata
        val trainedDataFile = File(tessdataDir, "eng.traineddata")

        // ✅ 1. tessdata 폴더 생성
        if (!tessdataDir.exists()) {
            val created = tessdataDir.mkdirs()
            Log.d("OCR", "tessdata 디렉토리 생성됨: $created")
        }

        // ✅ 2. eng.traineddata 복사
        if (!trainedDataFile.exists()) {
            try {
                context.assets.open("tessdata/eng.traineddata").use { input ->
                    FileOutputStream(trainedDataFile).use { output ->
                        input.copyTo(output)
                    }
                }
                Log.d("OCR", "eng.traineddata 복사 완료")
            } catch (e: Exception) {
                Log.e("OCR", "eng.traineddata 복사 실패", e)
                throw RuntimeException("eng.traineddata 복사 실패")
            }
        } else {
            Log.d("OCR", "eng.traineddata 이미 존재함")
        }

        // ✅ 3. 정확한 경로로 초기화 시도
        val dataPath = basePath  // 여기에 tessdata가 있어야 함
        tessBaseAPI = TessBaseAPI()

        val success = tessBaseAPI?.init(dataPath, "eng") ?: false
        if (!success) {
            throw IllegalStateException("Tesseract 초기화 실패: 경로=$dataPath, eng.traineddata 존재=${trainedDataFile.exists()}")
        }

        Log.d("OCR", "Tesseract 초기화 성공")
    }

    fun getText(bitmap: Bitmap): String {
        tessBaseAPI?.setImage(bitmap)
        return tessBaseAPI?.utF8Text ?: ""
    }
}
