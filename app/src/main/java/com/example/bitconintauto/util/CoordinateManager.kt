package com.example.bitconintauto.util

import com.example.bitconintauto.model.Coordinate

object CoordinateManager {
    private val map = mutableMapOf<String, MutableList<Coordinate>>()

    fun set(label: String, coord: Coordinate) {
        map.getOrPut(label) { mutableListOf() }.clear()
        map[label]?.add(coord)
    }

    fun get(label: String): List<Coordinate> {
        return map[label] ?: emptyList()
    }

    fun getPrimaryCoordinate(): Coordinate? {
        return map["trigger"]?.firstOrNull()
    }
}
