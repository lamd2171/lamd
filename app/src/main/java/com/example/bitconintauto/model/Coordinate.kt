package com.example.bitconintauto.model

data class Coordinate(
    val x: Int,
    val y: Int,
    val width: Int = 100,
    val height: Int = 60,
    val label: String = "",
    val expectedValue: String = ""
) {
    fun centerX() = x + width / 2
    fun centerY() = y + height / 2
}
