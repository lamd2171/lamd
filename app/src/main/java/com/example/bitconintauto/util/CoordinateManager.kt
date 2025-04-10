package com.example.bitconintauto.util

data class Coordinate(val x: Int, val y: Int, val description: String = "")

object CoordinateManager {
    private val coordinates = mutableListOf<Coordinate>()

    fun addCoordinate(x: Int, y: Int, description: String = "") {
        coordinates.add(Coordinate(x, y, description))
    }

    fun getCoordinates(): List<Coordinate> = coordinates

    fun reset() {
        coordinates.clear()
    }
}
