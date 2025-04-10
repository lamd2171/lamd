package com.example.bitconintauto.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream
import java.text.DecimalFormat

object Utils {

    fun cropBitmap(source: Bitmap, rect: Rect): Bitmap {
        val safeRect = Rect(
            rect.left.coerceAtLeast(0),
            rect.top.coerceAtLeast(0),
            rect.right.coerceAtMost(source.width),
            rect.bottom.coerceAtMost(source.height)
        )
        return Bitmap.createBitmap(
            source,
            safeRect.left,
            safeRect.top,
            safeRect.width(),
            safeRect.height()
        )
    }

    fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun base64ToBitmap(encoded: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(encoded, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            Log.e("Utils", "base64ToBitmap error: ${e.message}")
            null
        }
    }

    fun isValidNumber(text: String): Boolean {
        return text.trim().matches(Regex("^[0-9]+(\\.[0-9]+)?$"))
    }

    fun formatDecimal(value: Double, digits: Int = 6): String {
        val pattern = "0.${"#".repeat(digits)}"
        val formatter = DecimalFormat(pattern)
        return formatter.format(value)
    }
}
