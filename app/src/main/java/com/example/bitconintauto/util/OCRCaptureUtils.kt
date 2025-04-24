// OCRCaptureUtils.kt
package com.example.bitconintauto.util

import android.graphics.*
import android.util.Log
import com.example.bitconintauto.ocr.TesseractManager
import com.googlecode.tesseract.android.TessBaseAPI
import kotlin.math.min
import java.io.File
import java.io.FileOutputStream


object OCRCaptureUtils {

    fun extractTextFromBitmap(bitmap: Bitmap): String {
        return try {
            val grayBitmap = convertToGrayscale(bitmap)
            val raw = TesseractManager.recognizeText(grayBitmap).trim()
            grayBitmap.recycle()
            normalizeOcrText(raw)
        } catch (e: Exception) {
            Log.e("OCR", "❌ 전체 OCR 실패: ${e.message}")
            ""
        }
    }

    fun extractTextFromBitmapRegion(bitmap: Bitmap, rect: Rect): String {
        return try {
            val region = Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height())
            val grayRegion = convertToGrayscale(region)

            // 디버깅용 비트맵 저장
            saveBitmapToFile(region, "ocr_region_raw.png")
            saveBitmapToFile(grayRegion, "ocr_region_gray.png")

            val raw = TesseractManager.recognizeText(grayRegion).trim()
            region.recycle()
            grayRegion.recycle()
            normalizeOcrText(raw)
        } catch (e: Exception) {
            Log.e("OCR", "❌ 영역 OCR 실패: ${e.message}")
            ""
        }
    }

    private fun saveBitmapToFile(bitmap: Bitmap, fileName: String) {
        try {
            val path = "/sdcard/Download/$fileName"
            val file = java.io.File(path)
            val out = java.io.FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
            Log.d("OCR_DEBUG", "✅ 비트맵 저장됨: $path")
        } catch (e: Exception) {
            Log.e("OCR_DEBUG", "❌ 비트맵 저장 실패: ${e.message}")
        }
    }


    fun findWordRectFromBitmap(bitmap: Bitmap, word: String, normalize: Boolean = true): Rect? {
        val tess = getTessBaseAPI() ?: return null
        val grayBitmap = convertToGrayscale(bitmap)

        return try {
            tess.setImage(grayBitmap)
            tess.getUTF8Text()

            val iterator = tess.resultIterator ?: return null
            val level = TessBaseAPI.PageIteratorLevel.RIL_WORD
            val candidates = mutableListOf<Rect>()

            Log.d("OCR_DEBUG", "✅ setImage + recognize 완료")

            iterator.begin()
            do {
                val wordText = iterator.getUTF8Text(level)
                val wordRect = iterator.getBoundingRect(level)
                Log.d("OCR_DEBUG", "단어: '$wordText' → 위치: $wordRect")

                val actual = if (normalize) normalizeOcrText(wordText) else wordText
                val expected = if (normalize) normalizeOcrText(word) else word

                if (!actual.isNullOrBlank() && isValueMatched(actual, expected, "==", normalize = false)) {
                    if (wordRect.width() > 10 && wordRect.height() > 10) {
                        Log.d("OCR_DEBUG", "✅ 유효한 단어 영역 발견: '$wordText' → $wordRect")
                        candidates.add(wordRect)
                    } else {
                        Log.w("OCR_DEBUG", "⚠️ 영역 크기 무시됨: $wordRect")
                    }
                }

            } while (iterator.next(level))

            candidates.minByOrNull { it.left }
        } catch (e: Exception) {
            Log.e("OCR_DEBUG", "❌ 단어 찾기 실패: ${e.message}")
            null
        } finally {
            grayBitmap.recycle()
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
        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        canvas.drawBitmap(src, 0f, 0f, paint)
        return grayscale
    }

    fun extractNumberBeforePicn(text: String): Double {
        val regexList = listOf(
            Regex("Sent\\s+([0-9]+\\.[0-9]+)\\s+(P|Pl|PlC|PlCN|PICN)", RegexOption.IGNORE_CASE),
            Regex("<\\s*([0-9]+\\.[0-9]+)\\s*(P|Pl|PlC|PlCN|PICN)\\s*>", RegexOption.IGNORE_CASE),
            Regex("([0-9]+\\.[0-9]+)(P|Pl|PlC|PlCN|PICN)", RegexOption.IGNORE_CASE)
        )
        for (regex in regexList) {
            val match = regex.find(text)
            if (match != null) {
                return match.groups[1]?.value?.toDoubleOrNull() ?: 0.0
            }
        }
        return 0.0
    }


    fun isValueMatched(actual: String, expected: String, comparator: String, normalize: Boolean = true): Boolean {
        val normActual = if (normalize) normalizeOcrText(actual) else actual
        val normExpected = if (normalize) normalizeOcrText(expected) else expected

        return when (comparator) {
            "==" -> {
                normActual.equals(normExpected, ignoreCase = true)
                        || levenshtein(normActual.lowercase(), normExpected.lowercase()) <= 2
            }
            else -> false
        }
    }


    private fun normalizeOcrText(text: String): String {
        return text
            .replace("ﬂ", "fl", ignoreCase = true)
            .replace("ﬁ", "fi", ignoreCase = true)
            .replace("l|", "ll", ignoreCase = true)
            .replace("lI", "ll", ignoreCase = true)
            .replace("I", "l", ignoreCase = true)
            .replace("’", "'", ignoreCase = true)
            .replace("“", "\"", ignoreCase = true)
            .replace("”", "\"", ignoreCase = true)
            .replace("[^\\p{L}\\p{N}\\s\\.]".toRegex(), "") // 유니코드 글자+숫자+공백+소수점 유지
            .trim()
    }

    private fun levenshtein(lhs: String, rhs: String): Int {
        val dp = Array(lhs.length + 1) { IntArray(rhs.length + 1) }
        for (i in lhs.indices) dp[i + 1][0] = i + 1
        for (j in rhs.indices) dp[0][j + 1] = j + 1
        for (i in lhs.indices) {
            for (j in rhs.indices) {
                val cost = if (lhs[i] == rhs[j]) 0 else 1
                dp[i + 1][j + 1] = min(
                    min(dp[i][j + 1] + 1, dp[i + 1][j] + 1),
                    dp[i][j] + cost
                )
            }
        }
        return dp[lhs.length][rhs.length]
    }
}
