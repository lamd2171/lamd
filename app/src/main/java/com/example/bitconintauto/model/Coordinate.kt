package com.example.bitconintauto.model

data class Coordinate(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
    val label: String,
    val expectedValue: String = "" // 이 부분이 있어야 함
) {
    // centerX()와 centerY() 추가
    fun centerX() = x + width / 2
    fun centerY() = y + height / 2
}
