package com.example.bitconintauto.ocr

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.File
import java.io.FileOutputStream

object TesseractManager {

    private var tessBaseAPI: TessBaseAPI? = null
    private const val TAG = "OCR"
    private const val LANG = "kor+eng"

    fun init(context: Context) {
        val tessDir = File(context.filesDir, "tesseract/tessdata")
        if (!tessDir.exists()) {
            tessDir.mkdirs()
        }

        val languages = listOf("kor", "eng")
        for (lang in languages) {
            val trainedDataFile = File(tessDir, "$lang.traineddata")
            if (!trainedDataFile.exists()) {
                try {
                    context.assets.open("tessdata/$lang.traineddata").use { input ->
                        FileOutputStream(trainedDataFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "❌ OCR 학습 파일 복사 실패 ($lang): ${e.message}")
                }
            }
        }

        try {
            val tessPath = File(context.filesDir, "tesseract").absolutePath
            tessBaseAPI = TessBaseAPI()
            val success = tessBaseAPI?.init(tessPath, LANG) ?: false
            if (success) {
                Log.d(TAG, "✅ Tesseract 초기화 성공")
            } else {
                Log.e(TAG, "❌ Tesseract 초기화 실패")
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ 전체 OCR 실패: ${e.message}")
        }
    }

    fun recognizeText(bitmap: Bitmap): String {
        return try {
            tessBaseAPI?.setImage(bitmap)
            val text = tessBaseAPI?.utF8Text ?: ""
            Log.d(TAG, "🧠 OCR 결과: $text")
            text
        } catch (e: Exception) {
            Log.e(TAG, "❌ OCR 인식 실패: ${e.message}")
            ""
        }
    }

    fun release() {
        tessBaseAPI?.end()
        tessBaseAPI = null
    }
}
