package com.example.bitconintauto.core

data class Coordinate(
    val x: Int,
    val y: Int,
    val label: String = "",
    var expectedValue: String? = null
)
