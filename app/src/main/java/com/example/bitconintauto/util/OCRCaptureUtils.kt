package com.example.bitconintauto.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.media.projection.MediaProjection
import android.util.Log
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.File
import java.io.FileOutputStream
import java.util.regex.Pattern

object OCRCaptureUtils {

    private val tessBaseAPI by lazy {
        TessBaseAPI().apply {
            val tessDataPath = initTesseractDataPath()
            init(tessDataPath, "eng")
        }
    }

    private fun initTesseractDataPath(): String {
        val basePath = "/data/data/com.example.bitconintauto/files/tesseract"
        val tessdataDir = File("$basePath/tessdata")
        if (!tessdataDir.exists()) {
            tessdataDir.mkdirs()
        }
        val trainedDataFile = File(tessdataDir, "eng.traineddata")
        if (!trainedDataFile.exists()) {
            val inputStream = OCRCaptureUtils::class.java.classLoader!!
                .getResourceAsStream("assets/tessdata/eng.traineddata")
            val outputStream = FileOutputStream(trainedDataFile)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
        }
        return basePath
    }

    // ✅ 전체 화면에서 텍스트 추출 (MediaProjection 직접 전달)
    fun extractTextFromFullScreen(context: Context, projection: MediaProjection): String {
        val bitmap = ScreenCaptureHelper.captureScreen(context, projection)
        if (bitmap == null) {
            Log.e("OCR", "❌ 캡처 실패: fullBitmap == null")
            return ""
        }
        val text = extractTextFromBitmap(bitmap)
        saveDebugImage(context, bitmap, "ocr_fullscreen_debug.png")
        return text
    }

    // ✅ 특정 영역에서 텍스트 추출 (기존 유지)
    fun extractTextFromRegion(context: Context, rect: Rect): String {
        val fullBitmap = ScreenCaptureHelper.captureScreen(context)
        if (fullBitmap == null) {
            Log.e("OCR", "❌ 캡처 실패: regionBitmap == null")
            return ""
        }

        val clipped = Bitmap.createBitmap(
            fullBitmap,
            rect.left.coerceAtLeast(0),
            rect.top.coerceAtLeast(0),
            rect.width().coerceAtMost(fullBitmap.width - rect.left),
            rect.height().coerceAtMost(fullBitmap.height - rect.top)
        )

        val text = extractTextFromBitmap(clipped)
        saveDebugImage(context, clipped, "ocr_region_debug.png")
        return text
    }

    // ✅ OCR 수행
    fun extractTextFromBitmap(bitmap: Bitmap): String {
        return try {
            tessBaseAPI.setImage(bitmap)
            tessBaseAPI.utF8Text ?: ""
        } catch (e: Exception) {
            Log.e("OCR", "❌ OCR 실패: ${e.message}")
            ""
        }
    }

    // ✅ OCR 값 비교
    fun isValueMatched(text: String, target: String?, operator: String?): Boolean {
        if (target.isNullOrEmpty() || operator.isNullOrEmpty()) return false

        return when (operator) {
            "EQUALS" -> text.trim() == target
            "CONTAINS" -> text.contains(target, ignoreCase = true)
            "STARTS_WITH" -> text.trim().startsWith(target, ignoreCase = true)
            "ENDS_WITH" -> text.trim().endsWith(target, ignoreCase = true)
            else -> false
        }
    }

    // ✅ "PICN" 앞의 숫자 추출
    fun extractNumberBeforePicn(text: String): Double {
        val regex = """(\d+(?:\.\d+)?)(?=\s*PICN)"""
        val pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(text)
        return if (matcher.find()) {
            matcher.group(1)?.toDoubleOrNull() ?: 0.0
        } else {
            0.0
        }
    }

    // ✅ 디버그 이미지 저장
    private fun saveDebugImage(context: Context, bitmap: Bitmap, filename: String) {
        try {
            val file = File(context.cacheDir, filename)
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            Log.d("OCR", "📁 디버그 이미지 저장 위치: ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e("OCR", "❌ 디버그 이미지 저장 실패: ${e.message}")
        }
    }
}
