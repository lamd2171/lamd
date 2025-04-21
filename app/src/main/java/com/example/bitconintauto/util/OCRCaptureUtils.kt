package com.example.bitconintauto.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import com.example.bitconintauto.ocr.TesseractManager
import com.example.bitconintauto.util.ScreenCaptureHelper.captureScreen

object OCRCaptureUtils {
    private var tessManager: TesseractManager? = null

    /**
     * 주어진 영역을 캡처한 후 OCR로 텍스트를 추출한다.
     */
    fun extractValue(context: Context, rect: Rect): String {
        if (tessManager == null) tessManager = TesseractManager(context)
        val mediaProjection = PermissionUtils.getMediaProjection()
        if (mediaProjection == null) {
            Log.e("ScreenCaptureHelper", "❌ MediaProjection이 null임")
            return ""
        }
        val bitmap: Bitmap? = captureScreen(context, rect)


        return if (bitmap != null) {
            val ocrResult = tessManager?.getTextFromBitmap(bitmap)
            Log.d("OCR", "🧠 OCR 추출 결과: $ocrResult")
            ocrResult ?: ""
        } else {
            Log.e("OCR", "❌ 캡처 실패: bitmap == null")
            ""
        }
    }

    /**
     * OCR 추출 텍스트가 기준 텍스트와 일치하는지 비교한다.
     * @param ocrText 인식된 텍스트
     * @param target 기준 텍스트
     * @param operator 비교 방식: ==, >=, >, <=, <, !=
     */
    fun isValueMatched(ocrText: String, target: String, operator: String): Boolean {
        val ocrValue = ocrText.filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: return false
        val targetValue = target.toDoubleOrNull() ?: return false

        val result = when (operator) {
            "==" -> ocrValue == targetValue
            ">=" -> ocrValue >= targetValue
            ">"  -> ocrValue > targetValue
            "<=" -> ocrValue <= targetValue
            "<"  -> ocrValue < targetValue
            "!=" -> ocrValue != targetValue
            else -> false
        }

        Log.d("OCR", "🔍 비교 결과: $ocrValue $operator $targetValue => $result")
        return result
    }
}
