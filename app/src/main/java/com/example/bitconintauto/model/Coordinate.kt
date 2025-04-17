package com.example.bitconintauto.model

import android.graphics.Rect

/**
 * 자동화에 사용되는 좌표 정보 클래스
 *
 * @param x 좌표의 x값
 * @param y 좌표의 y값
 * @param width OCR 인식 영역의 가로 길이
 * @param height OCR 인식 영역의 세로 길이
 * @param label 좌표에 대한 설명 (예: "트리거", "주소 클릭")
 * @param expectedValue OCR로 인식되길 기대하는 값
 * @param comparator 비교 연산자 (예: "==", ">=", "<=" 등)
 * @param type 좌표의 역할 (PRIMARY, CLICK, COPY, 등)
 * @param step 자동화 루틴 순서 (PRIMARY가 아닌 경우에만 사용)
 */
data class Coordinate(
    val x: Int,
    val y: Int,
    val width: Int = 100,
    val height: Int = 50,
    val label: String = "",
    val expectedValue: String = "",
    val comparator: String = "==",
    val type: CoordinateType = CoordinateType.PRIMARY,
    val step: Int = -1
) {
    /**
     * 좌표 + 크기를 기반으로 Rect 반환
     */
    fun toRect(): Rect {
        return Rect(x, y, x + width, y + height)
    }

    /**
     * OCR 비교 시 사용할 기준 텍스트
     */
    val targetText: String
        get() = expectedValue

    /**
     * OCR 비교 시 사용할 연산자
     */
    val compareOperator: String
        get() = comparator
}
