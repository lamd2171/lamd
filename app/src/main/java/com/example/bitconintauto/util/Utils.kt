package com.example.bitconintauto.util

import android.graphics.Rect
import com.example.bitconintauto.model.Coordinate

object Utils {

    fun Coordinate.toRect(): Rect {
        return Rect(x, y, x + width, y + height)
    }

    fun isValidInteger(value: String?): Boolean {
        return value?.toIntOrNull()?.let { it >= 1 } == true
    }

    fun getThreshold(): Float {
        return 0.001f // 0.001 더하는 계산 기준값
    }
}
