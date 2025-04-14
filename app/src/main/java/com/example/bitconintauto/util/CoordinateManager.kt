package com.example.bitconintauto.util

import com.example.bitconintauto.model.Coordinate

object CoordinateManager {
    private val coordinates = mutableMapOf<String, MutableList<Coordinate>>()

    init {
        // 기본 트리거 좌표 등록 (LDPlayer 기준)
        set("trigger", Coordinate(x = 200, y = 120, width = 140, height = 60))
    }

    fun get(label: String): List<Coordinate> = coordinates[label] ?: emptyList()

    fun set(label: String, coordinate: Coordinate) {
        val list = coordinates.getOrPut(label) { mutableListOf() }
        list.clear()
        list.add(coordinate)
    }

    fun add(label: String, coordinate: Coordinate) {
        coordinates.getOrPut(label) { mutableListOf() }.add(coordinate)
    }

    fun getPrimaryCoordinate(): Coordinate? = get("trigger").firstOrNull()

    fun getClickPathSequence(): List<Coordinate> = listOf(
        "step2", "step3", "step4", "step5", "step6", "step7", "step8", "step9", "step10"
    ).mapNotNull { get(it).firstOrNull() }

    fun getCopyTarget(): Coordinate? = get("step12").firstOrNull()
    fun getPasteTarget(): Coordinate? = get("orderPriceField").firstOrNull()
    fun getFinalClickCoordinates(): List<Coordinate> = listOf(
        "step14", "step15", "step16", "stepQtyMax", "stepSell", "stepSellConfirm", "stepPay"
    ).mapNotNull { get(it).firstOrNull() }

    fun getThreshold(): Double = 0.001
}
