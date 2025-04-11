package com.example.bitconintauto.util

object ValueChangeDetector {
    fun hasSignificantChange(previous: Double?, current: Double?, threshold: Double): Boolean {
        if (previous == null || current == null) return false
        return kotlin.math.abs(current - previous) >= threshold
    }
}
