package com.example.bitconintauto.util

import com.example.bitconintauto.model.Coordinate

object CoordinateManager {
    private val coordinateMap: MutableMap<String, MutableList<Coordinate>> = mutableMapOf()

    fun set(label: String, coordinate: Coordinate) {
        coordinateMap.getOrPut(label) { mutableListOf() }.add(coordinate)
    }

    fun get(label: String): List<Coordinate> {
        return coordinateMap[label] ?: emptyList()
    }

    fun getPrimaryCoordinate(): Coordinate? {
        return coordinateMap["trigger"]?.firstOrNull()
    }

    fun clear() {
        coordinateMap.clear()
    }
}
