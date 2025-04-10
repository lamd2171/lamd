package com.example.bitconintauto.util

import android.graphics.Bitmap
import com.example.bitconintauto.ocr.OCRProcessor

object NumberDetector {

    private val ocrEngine = OCRProcessor()

    fun detectNumberAt(bitmap: Bitmap): Double? {
        val rawText = ocrEngine.getText(bitmap)
        return rawText.trim().toDoubleOrNull()
    }

    fun readValueAt(bitmap: Bitmap): String {
        return ocrEngine.getText(bitmap).trim()
    }
}
