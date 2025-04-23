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

    @Synchronized
    fun init(context: Context) {
        try {
            val tessDataDir = File(context.filesDir, "tesseract/tessdata")
            val tessPath = context.filesDir.absolutePath + "/tesseract/"

            Log.d(TAG, "📁 내부 OCR 경로: $tessPath")
            Log.d(TAG, "📁 traineddata 파일 위치: ${tessDataDir.absolutePath}")

            if (!tessDataDir.exists()) {
                val made = tessDataDir.mkdirs()
                Log.d(TAG, "📂 tessdata 폴더 생성됨: $made")
            }

            val languages = listOf("kor", "eng")
            for (lang in languages) {
                val trainedDataFile = File(tessDataDir, "$lang.traineddata")
                if (!trainedDataFile.exists()) {
                    try {
                        context.assets.open("tessdata/$lang.traineddata").use { input ->
                            FileOutputStream(trainedDataFile).use { output ->
                                input.copyTo(output)
                            }
                        }
                        Log.d(TAG, "✅ $lang.traineddata 복사 완료")
                    } catch (e: Exception) {
                        Log.e(TAG, "❌ $lang.traineddata 복사 실패: ${e.message}")
                    }
                } else {
                    Log.d(TAG, "✅ $lang.traineddata 이미 존재함")
                }
            }

            tessBaseAPI = TessBaseAPI()
            val initResult = tessBaseAPI?.init(tessPath, LANG) ?: false
            Log.d(TAG, "✅ Tesseract 초기화: $initResult")

        } catch (e: Exception) {
            Log.e(TAG, "❌ 전체 OCR 실패: ${e.message}")
        }
    }

    @Synchronized
    fun recognizeText(bitmap: Bitmap): String {
        if (tessBaseAPI == null) {
            Log.e(TAG, "❌ Tesseract가 초기화되지 않음!")
            return ""
        }
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

    @Synchronized
    fun release() {
        tessBaseAPI?.end()
        tessBaseAPI = null
    }
}
