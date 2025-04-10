package com.example.bitconintauto.util

object NumberUtils {
    fun isPositiveNumber(text: String?): Boolean {
        return try {
            val number = text?.toDoubleOrNull() ?: return false
            number > 1
        } catch (e: Exception) {
            false
        }
    }

    fun addOffset(value: String?, offset: Double): String {
        return try {
            val number = value?.toDoubleOrNull() ?: return ""
            String.format("%.6f", number + offset)
        } catch (e: Exception) {
            ""
        }
    }
}
