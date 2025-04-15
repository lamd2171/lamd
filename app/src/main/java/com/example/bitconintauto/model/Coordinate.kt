package com.example.bitconintauto.model

data class Coordinate(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
    val label: String,
    val expectedValue: String = "" // 이 부분이 있어야 함
)
