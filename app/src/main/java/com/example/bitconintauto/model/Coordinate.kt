// [4] app/src/main/java/com/example/bitconintauto/model/Coordinate.kt

package com.example.bitconintauto.model

data class Coordinate(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
    val label: String = "",
    val expectedValue: String = ""   // ✅ 추가
) {
    fun toRect() = android.graphics.Rect(x, y, x + width, y + height)
}