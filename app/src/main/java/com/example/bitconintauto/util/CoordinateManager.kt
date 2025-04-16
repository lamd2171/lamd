package com.example.bitconintauto.util

import com.example.bitconintauto.model.Coordinate

object CoordinateManager {

    private val coordinateMap: MutableMap<String, MutableList<Coordinate>> = mutableMapOf()

    fun register(label: String, coord: Coordinate) {
        val list = coordinateMap.getOrPut(label) { mutableListOf() }
        list.add(coord)
    }

    fun get(label: String): List<Coordinate> {
        return coordinateMap[label] ?: emptyList()
    }

    fun clear(label: String) {
        coordinateMap[label]?.clear()
    }

    fun clearAll() {
        coordinateMap.clear()
    }

    fun getAll(): Map<String, List<Coordinate>> {
        return coordinateMap
    }
}
