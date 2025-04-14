
// ✅ CoordinateManager.kt - step2~26 등록 구조 유지 (트리거 중심 순차 실행 전용)

package com.example.bitconintauto.util

import com.example.bitconintauto.model.Coordinate

object CoordinateManager {
    private val coordinates = mutableMapOf<String, MutableList<Coordinate>>()

    fun get(label: String): List<Coordinate> = coordinates[label] ?: emptyList()

    fun set(label: String, coordinate: Coordinate) {
        coordinates.getOrPut(label) { mutableListOf() }.apply {
            clear()
            add(coordinate)
        }
    }

    fun add(label: String, coordinate: Coordinate) {
        coordinates.getOrPut(label) { mutableListOf() }.add(coordinate)
    }

    fun getPrimaryCoordinate(): Coordinate? = get("trigger").firstOrNull()

    fun getClickPathSequence(): List<Coordinate> = listOf(
        "step2", "step3", "step4", "step5", "step6",
        "step7", "step8", "step9", "step10"
    ).mapNotNull { get(it).firstOrNull() }

    fun getCopyTarget(): Coordinate? = get("step13").firstOrNull()
    fun getPasteTarget(): Coordinate? = get("step20").firstOrNull()
    fun getThreshold(): Double = 0.001

    fun getFinalClickCoordinates(): List<Coordinate> = listOf(
        "step22", "step23", "step24", "step25", "step26"
    ).mapNotNull { get(it).firstOrNull() }
}
