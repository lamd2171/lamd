package com.example.bitconintauto.model

/**
 * 좌표 유형 정의 클래스
 * - CLICK: 클릭 좌표
 * - SCROLL: 스크롤 위치
 * - COPY: 복사 대상
 * - PASTE: 붙여넣기 대상
 * - PRIMARY: OCR 트리거 대상
 */
enum class CoordinateType {
    PRIMARY,
    CLICK,
    COPY,
    PASTE,
    SCROLL
}
