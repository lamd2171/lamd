package com.example.bitconintauto.model

data class Coordinate(
    val x: Int,
    val y: Int,
    val width: Int = 100,
    val height: Int = 60,
    val label: String = ""
    val expectedValue: String = ""  // 추가
) {
    fun centerX(): Int = x + width / 2
    fun centerY(): Int = y + height / 2
}
