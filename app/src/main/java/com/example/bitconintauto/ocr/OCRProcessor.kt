package com.example.bitconintauto.ocr

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import cz.adaptech.tesseract4android.Tesseract

class OCRProcessor {

    private lateinit var tesseract: Tesseract

    fun init(context: Context) {
        try {
            tesseract = Tesseract(context)
            tesseract.init("eng") // 언어 설정
        } catch (e: Exception) {
            Log.e("OCRProcessor", "Tesseract 초기화 실패: ${e.message}")
        }
    }

    fun getText(bitmap: Bitmap): String {
        return try {
            tesseract.setImage(bitmap)
            tesseract.utF8Text ?: ""
        } catch (e: Exception) {
            Log.e("OCRProcessor", "OCR 처리 실패: ${e.message}")
            ""
        }
    }

    fun stop() {
        try {
            tesseract.recycle()
        } catch (e: Exception) {
            Log.e("OCRProcessor", "Tesseract 정리 실패: ${e.message}")
        }
    }
}
