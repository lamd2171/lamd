package com.example.bitconintauto.service

import android.content.Context
import android.util.Log
import com.example.bitconintauto.model.Coordinate
import com.example.bitconintauto.ocr.OCRProcessor
import com.example.bitconintauto.util.ClickSimulator
import com.example.bitconintauto.util.CoordinateManager
import com.example.bitconintauto.util.Utils

object AutoClicker {

    fun executeAutomation(context: Context) {
        Log.d("AutoClicker", "자동화 루틴 시작")

        // 1. 트리거 좌표 (PRIMARY)
        val trigger = CoordinateManager.get("primary").firstOrNull() ?: return
        val triggerText = OCRProcessor.getText(context, trigger)
        val triggerValue = triggerText?.toDoubleOrNull()
        if (triggerValue == null || triggerValue < 1 || triggerText.contains(".")) {
            Log.d("AutoClicker", "트리거 값 조건 불충족: $triggerText")
            return
        }

        // 2. 클릭 경로 실행
        CoordinateManager.get("click").forEach {
            ClickSimulator.performClick(it.x, it.y)
            Thread.sleep(700)
        }

        // 3. 복사 대상 텍스트 추출
        val copyTarget = CoordinateManager.get("copy").firstOrNull()
        val copiedText = copyTarget?.let { OCRProcessor.getText(context, it) } ?: return
        Log.d("AutoClicker", "복사된 텍스트: $copiedText")

        // 4. 계산 수행 (0.001 더함)
        val calculated = Utils.addValueSafely(copiedText, 0.001)
        Log.d("AutoClicker", "계산 결과: $calculated")

        // 5. 붙여넣기 좌표 입력
        val pasteTarget = CoordinateManager.get("paste").firstOrNull()
        if (pasteTarget != null) {
            ClickSimulator.inputText(pasteTarget.x, pasteTarget.y, calculated)
        }

        // 6. 최종 클릭 (send 등)
        CoordinateManager.get("final").forEach {
            ClickSimulator.performClick(it.x, it.y)
            Thread.sleep(500)
        }

        Log.d("AutoClicker", "자동화 루틴 완료")
    }
}
