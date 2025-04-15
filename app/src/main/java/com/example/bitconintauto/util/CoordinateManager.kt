package com.example.bitconintauto.util

import com.example.bitconintauto.model.Coordinate

object CoordinateManager {
    private val map: MutableMap<String, MutableList<Coordinate>> = mutableMapOf()

    fun get(label: String): List<Coordinate> = map[label] ?: emptyList()

    fun add(label: String, coordinate: Coordinate) {
        if (!map.containsKey(label)) {
            map[label] = mutableListOf()
        }
        map[label]?.add(coordinate)
    }

    fun getPrimaryCoordinate(): Coordinate? = get("trigger").firstOrNull()
}
