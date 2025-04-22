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
import android.accessibilityservice.AccessibilityService.GestureResultCallback

object OCRCaptureUtils {

    private val tessBaseAPI by lazy {
        TessBaseAPI().apply {
            val tessDataPath = initTesseractDataPath()
            init(tessDataPath, "eng")
        }
    }

    // Tesseract 데이터 경로 초기화
    private fun initTesseractDataPath(): String {
        val basePath = "/data/data/com.example.bitconintauto/files/tesseract"
        val tessdataDir = File("$basePath/tessdata")
        if (!tessdataDir.exists()) tessdataDir.mkdirs()

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

    // 전체 화면 텍스트 추출
    fun extractTextFromFullScreen(context: Context, projection: MediaProjection): String? {
        val bitmap = ScreenCaptureHelper.captureScreen(context, projection)
        if (bitmap == null) {
            Log.e("OCRCaptureUtils", "❌ 화면 캡처 실패 (projection == null)")
            return null
        }
        val text = extractTextFromBitmap(bitmap)
        saveDebugImage(context, bitmap, "ocr_fullscreen_debug.png")
        return text
    }

    // 특정 영역 텍스트 추출
    fun extractTextFromRegion(context: Context, rect: Rect, projection: MediaProjection): String {
        val fullBitmap = ScreenCaptureHelper.captureScreen(context, projection)
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

    // OCR 텍스트 추출
    fun extractTextFromBitmap(bitmap: Bitmap): String {
        tessBaseAPI.pageSegMode = TessBaseAPI.PageSegMode.PSM_SINGLE_BLOCK
        return try {
            tessBaseAPI.setImage(bitmap)
            tessBaseAPI.utF8Text ?: ""
        } catch (e: Exception) {
            Log.e("OCR", "❌ OCR 실패: ${e.message}")
            ""
        }
    }

    // OCR 결과 비교
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

    // "PICN" 앞 숫자 추출
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

    // 전체화면에서 특정 단어의 위치(Rect) 추출
    fun getWordRectFromFullScreen(context: Context, projection: MediaProjection, word: String): Rect? {
        val bitmap = ScreenCaptureHelper.captureScreen(context, projection)
        if (bitmap == null) {
            Log.e("OCR", "❌ 전체화면 캡처 실패")
            return null
        }
        return findWordRectFromBitmap(bitmap, word)
    }

    // 특정 비트맵에서 단어 좌표 추출
    fun findWordRectFromBitmap(bitmap: Bitmap, target: String): Rect? {
        return try {
            tessBaseAPI.setImage(bitmap)
            tessBaseAPI.getHOCRText(0) // ✅ 내부 recognize 대체

            val iterator = tessBaseAPI.resultIterator
            val level = TessBaseAPI.PageIteratorLevel.RIL_WORD

            if (iterator != null) {
                do {
                    val word = iterator.getUTF8Text(level)
                    if (word != null && word.contains(target, ignoreCase = true)) {
                        Log.d("OCR", "✅ 단어 발견됨: $word")
                        return iterator.getBoundingRect(level)
                    }
                } while (iterator.next(level))
            } else {
                Log.e("OCR", "❌ ResultIterator가 null입니다")
            }

            null
        } catch (e: Exception) {
            Log.e("OCR", "❌ 단어 좌표 추출 실패: ${e.message}")
            null
        }
    }
    // OCRCaptureUtils.kt
    fun extractTextFromBitmapRegion(bitmap: Bitmap, region: Rect): String {
        val regionBitmap = Bitmap.createBitmap(
            bitmap,
            region.left.coerceAtLeast(0),
            region.top.coerceAtLeast(0),
            region.width().coerceAtMost(bitmap.width - region.left),
            region.height().coerceAtMost(bitmap.height - region.top)
        )
        return extractTextFromBitmap(regionBitmap)
    }

    // 디버그 이미지 저장
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
