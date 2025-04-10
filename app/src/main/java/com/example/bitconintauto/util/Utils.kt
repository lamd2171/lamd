package com.example.bitconintauto.util

import android.accessibilityservice.AccessibilityService
import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Base64
import com.example.bitconintauto.model.Coordinate
import com.example.bitconintauto.ocr.OCRProcessor
import java.io.ByteArrayOutputStream

object Utils {

    fun cropBitmap(original: Bitmap, cropRect: Rect): Bitmap {
        return Bitmap.createBitmap(
            original,
            cropRect.left,
            cropRect.top,
            cropRect.width(),
            cropRect.height()
        )
    }

    fun bitmapToBase64(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun base64ToBitmap(base64Str: String): Bitmap {
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        return android.graphics.BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    fun isValidNumber(text: String): Boolean {
        return text.toDoubleOrNull() != null
    }

    fun formatDecimal(value: Double, decimalPlaces: Int = 3): String {
        return "%.${decimalPlaces}f".format(value)
    }

    fun readValueAt(service: AccessibilityService, coordinate: Coordinate): Double? {
        val bitmap = OCRCaptureUtils.capture(service, coordinate) ?: return null
        val text = OCRProcessor().getText(bitmap)
        return text.toDoubleOrNull()
    }
}
