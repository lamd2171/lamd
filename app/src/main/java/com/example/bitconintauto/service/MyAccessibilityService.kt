package com.example.bitconintauto.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityEvent
import android.graphics.Bitmap
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import com.example.bitconintauto.ocr.OCRProcessor
import com.example.bitconintauto.logic.ExecutorManager
import com.example.bitconintauto.util.NumberDetector
import com.example.bitconintauto.util.ConditionChecker

class MyAccessibilityService : AccessibilityService() {

    private lateinit var executorManager: ExecutorManager
    private lateinit var ocrProcessor: OCRProcessor

    override fun onServiceConnected() {
        super.onServiceConnected()
        ocrProcessor = OCRProcessor()
        executorManager = ExecutorManager(this)
        Toast.makeText(this, "서비스 연결됨", Toast.LENGTH_SHORT).show()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        // 예시: 특정 페이지에 접근 후 OCR 실행
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            startAutomationLoop()
        }
    }

    override fun onInterrupt() {
        // 서비스 중단 처리
    }

    private fun startAutomationLoop() {
        executorManager.startCycle {
            performOCRAndAutomation()
        }
    }

    private fun performOCRAndAutomation() {
        // 예시: 좌표를 통해 OCR을 실행하고 조건 체크 후 자동화 진행
        val coordinates = listOf(Coordinate(100, 200), Coordinate(150, 250)) // 예시 좌표

        executorManager.stopCycle()  // 기존 루프 중지

        val bitmap: Bitmap? = captureScreenAtCoordinates(coordinates) // 캡처된 이미지
        val number = NumberDetector.detectNumberAt(bitmap ?: return)

        if (ConditionChecker.shouldTriggerAction(number.toFloat(), 1.0f)) {
            performClickAndAction(coordinates)
        } else {
            Toast.makeText(this, "조건 미충족", Toast.LENGTH_SHORT).show()
        }

        executorManager.startCycle {
            performOCRAndAutomation() // 루프 재시작
        }
    }

    private fun captureScreenAtCoordinates(coordinates: List<Coordinate>): Bitmap? {
        // 화면 캡처 메서드 구현, 예시로 좌표를 기반으로 Bitmap을 캡처
        return OCRCaptureUtils.capture(this, coordinates.first()) // 첫 번째 좌표 예시로 사용
    }

    private fun performClickAndAction(coordinates: List<Coordinate>) {
        // 좌표에 따른 자동화 동작 수행
        val autoClicker = AutoClicker(this)
        autoClicker.executeCycle(300, 500, "텍스트 예시", null) // 예시 실행
    }
}
