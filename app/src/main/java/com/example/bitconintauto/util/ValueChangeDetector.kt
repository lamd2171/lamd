package com.example.bitconintauto.util

object ValueChangeDetector {
    fun hasSignificantChange(
        previous: Double?,
        current: Double,
        threshold: Double = 0.001
    ): Boolean {
        return previous == null || kotlin.math.abs(current - previous) >= threshold
    }
}
