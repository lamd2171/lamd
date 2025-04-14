package com.example.bitconintauto.util

import com.example.bitconintauto.model.Coordinate

object CoordinateManager {
    private val coordinates = mutableMapOf<String, MutableList<Coordinate>>()

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
        "step2", "step3", "step4", "step5",
        "step6", "step7", "step8", "step9", "step10"
    ).mapNotNull { get(it).firstOrNull() }

    fun getCopyTarget(): Coordinate? = get("step12").firstOrNull()

    fun getPasteTarget(): Coordinate? = get("orderPriceField").firstOrNull()

    fun getFinalClickCoordinates(): List<Coordinate> = listOf(
        "step14", "step15", "step16",
        "stepQtyMax", "stepSell", "stepSellConfirm", "stepPay"
    ).mapNotNull { get(it).firstOrNull() }

    fun getScrollArea(): Coordinate? = get("scrollArea").firstOrNull()

    fun getThreshold(): Double = 0.001

    // ✅ 최초 기본 좌표 등록용
    init {
        // 트리거 박스 (15.2643... 들어가는 크기, 오전 이미지 기준)
        set("trigger", Coordinate(x = 80, y = 230, width = 400, height = 120))

        // 자동 클릭 순서 경로
        set("step2", Coordinate(x = 100, y = 400, width = 100, height = 80))
        set("step3", Coordinate(x = 100, y = 480, width = 100, height = 80))
        set("step4", Coordinate(x = 100, y = 560, width = 100, height = 80))
        set("step5", Coordinate(x = 100, y = 640, width = 100, height = 80))
        set("step6", Coordinate(x = 100, y = 720, width = 100, height = 80))
        set("step7", Coordinate(x = 200, y = 400, width = 100, height = 80))
        set("step8", Coordinate(x = 200, y = 480, width = 100, height = 80))
        set("step9", Coordinate(x = 200, y = 560, width = 100, height = 80))
        set("step10", Coordinate(x = 200, y = 640, width = 100, height = 80))

        // 복사 대상
        set("step12", Coordinate(x = 280, y = 320, width = 140, height = 80))

        // 붙여넣기 대상
        set("orderPriceField", Coordinate(x = 320, y = 600, width = 180, height = 80))

        // 최종 클릭 루틴
        set("step14", Coordinate(x = 360, y = 720, width = 100, height = 80))
        set("step15", Coordinate(x = 360, y = 780, width = 100, height = 80))
        set("step16", Coordinate(x = 360, y = 840, width = 100, height = 80))
        set("stepQtyMax", Coordinate(x = 400, y = 660, width = 100, height = 80))
        set("stepSell", Coordinate(x = 400, y = 720, width = 100, height = 80))
        set("stepSellConfirm", Coordinate(x = 400, y = 780, width = 100, height = 80))
        set("stepPay", Coordinate(x = 400, y = 840, width = 100, height = 80))

        // 스크롤 기준 영역 (조건 스크롤용 기준 박스)
        set("scrollArea", Coordinate(x = 200, y = 500, width = 300, height = 300))
    }
}
