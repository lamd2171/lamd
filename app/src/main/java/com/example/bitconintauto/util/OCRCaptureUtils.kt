package com.example.bitconintauto.util

import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import com.example.bitconintauto.service.CaptureForegroundService
import com.googlecode.tesseract.android.TessBaseAPI
import java.util.Locale

/**
 * OCR 및 화면 캡처 유틸리티 클래스
 */
object OCRCaptureUtils {
    private const val TAG = "OCR"
    private var tessBaseAPI: TessBaseAPI? = null

    /**
     * Tesseract 엔진 초기화
     */
    fun initTesseract(dataPath: String, lang: String = "eng") {
        tessBaseAPI = TessBaseAPI().apply {
            init(dataPath, lang)
        }
        Log.d(TAG, "✅ Tesseract 초기화 완료")
    }

    /**
     * 특정 영역을 캡처하고 OCR 수행
     */
    fun captureAndRecognizeText(region: Rect): String {
        Log.d(TAG, "📸 captureSync() 진입")
        val bitmap = CaptureForegroundService.captureScreen(region)
        if (bitmap == null) {
            Log.d("Executor", "⚠️ 캡처 실패: bitmap == null")
            return ""
        }

        tessBaseAPI?.setImage(bitmap)
        val recognizedText = tessBaseAPI?.utF8Text ?: ""
        return recognizedText.trim().replace("\n", "").replace(" ", "")
    }

    /**
     * 텍스트에서 숫자 추출 (예: 잔액, 수량 등)
     */
    fun extractValue(text: String): Double? {
        return text.filter { it.isDigit() || it == '.' }
            .toDoubleOrNull()
    }

    /**
     * 값 일치 여부 검사 (>= 조건 포함)
     */
    fun isValueMatched(actual: Double?, expected: Double?): Boolean {
        if (actual == null || expected == null) return false
        return actual >= expected
    }
}
