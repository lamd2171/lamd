package com.example.bitconintauto.util

object ConditionChecker {

    fun shouldTriggerAction(value: Double, threshold: Double = 1.0): Boolean {
        return value >= threshold
    }

    fun getUserOffset(): Double {
        // 기본 offset 값 (예: 0.001), 추후 사용자 입력 연동 가능
        return 0.001
    }
}
