package com.example.bitconintauto.util

import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import com.googlecode.tesseract.android.TessBaseAPI

object OCRCaptureUtils {
    private val tess: TessBaseAPI by lazy {
        TessBaseAPI().apply {
            val path = "/sdcard/tesseract/"
            init(path, "eng")
        }
    }

    fun extractTextFromBitmap(bitmap: Bitmap): String {
        return try {
            tess.setImage(bitmap)
            val text = tess.utF8Text ?: ""
            text.trim()
        } catch (e: Exception) {
            Log.e("OCR", "❌ 전체 OCR 실패: ${e.message}")
            ""
        }
    }

    fun extractTextFromBitmapRegion(bitmap: Bitmap, rect: Rect): String {
        return try {
            val region = Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height())
            tess.setImage(region)
            val text = tess.utF8Text ?: ""
            region.recycle()
            text.trim()
        } catch (e: Exception) {
            Log.e("OCR", "❌ 영역 OCR 실패: ${e.message}")
            ""
        }
    }

    fun findWordRectFromBitmap(bitmap: Bitmap, word: String): Rect? {
        return try {
            tess.setImage(bitmap)
            val iterator = tess.resultIterator ?: return null
            iterator.begin()
            do {
                val text = iterator.getUTF8Text(TessBaseAPI.PageIteratorLevel.RIL_WORD)
                if (text != null && text.contains(word, true)) {
                    val rect = iterator.getBoundingRect(TessBaseAPI.PageIteratorLevel.RIL_WORD)
                    Log.d("OCR", "✅ 단어 발견됨: $text → $rect")
                    return rect
                }
            } while (iterator.next(TessBaseAPI.PageIteratorLevel.RIL_WORD))
            null
        } catch (e: Exception) {
            Log.e("OCR", "❌ 단어 추출 실패: ${e.message}")
            null
        }
    }

    fun extractNumberBeforePicn(text: String): Double {
        val regex = Regex("Sent\\s+([0-9]+\\.[0-9]+)\\s+PICN")
        val match = regex.find(text)
        return match?.groups?.get(1)?.value?.toDoubleOrNull() ?: 0.0
    }

    fun isValueMatched(actual: String, expected: String, comparator: String): Boolean {
        return when (comparator) {
            "==" -> actual.trim().equals(expected.trim(), true)
            else -> false
        }
    }
}
