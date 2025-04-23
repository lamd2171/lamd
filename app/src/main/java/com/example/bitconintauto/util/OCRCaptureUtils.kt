// OCRCaptureUtils.kt
package com.example.bitconintauto.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import com.example.bitconintauto.ocr.TesseractManager
import com.googlecode.tesseract.android.TessBaseAPI

object OCRCaptureUtils {

    fun extractTextFromBitmap(bitmap: Bitmap): String {
        return try {
            val grayBitmap = convertToGrayscale(bitmap)
            TesseractManager.recognizeText(grayBitmap).trim().also {
                grayBitmap.recycle()
            }
        } catch (e: Exception) {
            Log.e("OCR", "❌ 전체 OCR 실패: ${e.message}")
            ""
        }
    }

    fun extractTextFromBitmapRegion(bitmap: Bitmap, rect: Rect): String {
        return try {
            val region = Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height())
            val grayRegion = convertToGrayscale(region)
            val text = TesseractManager.recognizeText(grayRegion).trim()
            region.recycle()
            grayRegion.recycle()
            text
        } catch (e: Exception) {
            Log.e("OCR", "❌ 영역 OCR 실패: ${e.message}")
            ""
        }
    }

    fun findWordRectFromBitmap(bitmap: Bitmap, word: String): Rect? {
        return try {
            val tess = getTessBaseAPI() ?: return null
            val grayBitmap = convertToGrayscale(bitmap)
            tess.setImage(grayBitmap)
            val iterator = tess.resultIterator ?: return null
            iterator.begin()
            do {
                val text = iterator.getUTF8Text(TessBaseAPI.PageIteratorLevel.RIL_WORD)
                if (text != null && text.contains(word, ignoreCase = true)) {
                    val rect = iterator.getBoundingRect(TessBaseAPI.PageIteratorLevel.RIL_WORD)
                    Log.d("OCR", "✅ 단어 발견됨: $text → $rect")
                    grayBitmap.recycle()
                    return rect
                }
            } while (iterator.next(TessBaseAPI.PageIteratorLevel.RIL_WORD))
            grayBitmap.recycle()
            null
        } catch (e: Exception) {
            Log.e("OCR", "❌ 단어 추출 실패: ${e.message}")
            null
        }
    }

    private fun getTessBaseAPI(): TessBaseAPI? {
        val field = TesseractManager::class.java.getDeclaredField("tessBaseAPI")
        field.isAccessible = true
        return field.get(TesseractManager) as? TessBaseAPI
    }

    private fun convertToGrayscale(src: Bitmap): Bitmap {
        val width = src.width
        val height = src.height
        val grayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(grayscale)
        val paint = Paint()
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f)
        val filter = ColorMatrixColorFilter(colorMatrix)
        paint.colorFilter = filter
        canvas.drawBitmap(src, 0f, 0f, paint)
        return grayscale
    }

    fun extractNumberBeforePicn(text: String): Double {
        val regex = Regex("Sent\\s+([0-9]+\\.[0-9]+)\\s+PICN")
        val match = regex.find(text)
        return match?.groups?.get(1)?.value?.toDoubleOrNull() ?: 0.0
    }

    fun isValueMatched(actual: String, expected: String, comparator: String): Boolean {
        return when (comparator) {
            "==" -> actual.trim().equals(expected.trim(), ignoreCase = true)
            else -> false
        }
    }
}
