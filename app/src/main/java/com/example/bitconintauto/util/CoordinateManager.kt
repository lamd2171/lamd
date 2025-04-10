package com.example.bitconintauto.util

import com.example.bitconintauto.model.Coordinate

object CoordinateManager {

    private val registeredCoordinates = mutableMapOf<String, List<Coordinate>>()

    fun register(name: String, coordinates: List<Coordinate>) {
        registeredCoordinates[name] = coordinates
    }

    fun get(name: String): List<Coordinate> {
        return registeredCoordinates[name] ?: emptyList()
    }

    fun getPrimaryCoordinate(): Coordinate? {
        return registeredCoordinates["primary"]?.firstOrNull()
    }

    fun getClickPathSequence(): List<Coordinate> {
        return registeredCoordinates["click"] ?: emptyList()
    }

    fun getCopyTarget(): Coordinate {
        return registeredCoordinates["copy"]?.firstOrNull() ?: Coordinate(0, 0)
    }

    fun getPasteTarget(): Coordinate {
        return registeredCoordinates["paste"]?.firstOrNull() ?: Coordinate(0, 0)
    }

    fun getUserOffset(): Float {
        return 0.001f // 기본 오프셋 값, 필요시 수정
    }

    fun getFinalActions(): List<Coordinate> {
        return registeredCoordinates["final"] ?: emptyList()
    }

    fun reset() {
        registeredCoordinates.clear()
    }
}
