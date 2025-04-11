package com.example.bitconintauto.util

import com.example.bitconintauto.model.Coordinate

object CoordinateManager {

    private val registeredCoordinates = mutableMapOf<String, List<Coordinate>>()
    private var onCoordinateChanged: (() -> Unit)? = null

    fun register(name: String, coordinates: List<Coordinate>) {
        registeredCoordinates[name] = coordinates
        onCoordinateChanged?.invoke()
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
        onCoordinateChanged?.invoke()
    }

    fun getThreshold(): Double {
        return 1.0
    }

    // ✅ 최초 클릭 여부 판단
    fun isFirstClick(): Boolean {
        return registeredCoordinates["primary"].isNullOrEmpty()
    }

    // ✅ 좌표 변경 리스너 설정
    fun setOnCoordinateChangedListener(listener: () -> Unit) {
        onCoordinateChanged = listener
    }

    fun removeCoordinateChangedListener() {
        onCoordinateChanged = null
    }
}
