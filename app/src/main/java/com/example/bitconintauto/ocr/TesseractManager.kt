package com.example.bitconintauto.ocr

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.File
import java.io.FileOutputStream

class TesseractManager(private val context: Context) {
    private var tessBaseApi: TessBaseAPI? = null
    private val lang = "eng" // 언어 설정

    init {
        initTesseract()
    }

    // Tesseract 초기화
    private fun initTesseract() {
        val dir = File(context.filesDir, "tesseract/tessdata")
        if (!dir.exists()) dir.mkdirs()

        val trainedData = File(dir, "$lang.traineddata")
        if (!trainedData.exists()) {
            context.assets.open("tessdata/$lang.traineddata").use { input ->
                FileOutputStream(trainedData).use { output ->
                    input.copyTo(output)
                }
            }
        }

        tessBaseApi = TessBaseAPI().apply {
            init(dir.parent, lang)
        }

        Log.d("OCR", "✅ Tesseract 초기화 완료")
    }

    // OCR 실행
    fun getTextFromBitmap(bitmap: Bitmap): String {
        return try {
            tessBaseApi?.setImage(bitmap)
            tessBaseApi?.getUTF8Text() ?: ""
        } catch (e: Exception) {
            Log.e("OCR", "❌ OCR 실패: ${e.message}")
            ""
        }
    }

    // 리소스 해제
    fun release() {
        tessBaseApi?.end()
    }
}
