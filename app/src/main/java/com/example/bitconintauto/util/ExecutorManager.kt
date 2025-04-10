package com.example.bitconintauto.util

import com.example.bitconintauto.ocr.OcrProcessor
import com.example.bitconintauto.service.AutoClickerService

class ExecutorManager(
    private val service: AutoClickerService,
    private val ocrProcessor: OcrProcessor
) {
    fun executeAutomation() {
        val number = ocrProcessor.recognizeNumberAt(100, 200) // 예시 좌표

        if (number >= PreferenceHelper.getThreshold(service)) {
            AppLogger.log("조건 충족: $number, 클릭 실행")
            service.performClick(300, 400) // 예시 클릭 좌표
        } else {
            AppLogger.log("조건 미충족: $number")
        }
    }
}
