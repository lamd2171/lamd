package com.example.bitconintauto.service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.widget.Toast
import com.example.bitconintauto.model.Coordinate
import com.example.bitconintauto.ocr.OCRProcessor
import com.example.bitconintauto.ui.OCRDebugOverlay
import com.example.bitconintauto.util.*
import kotlinx.coroutines.*

object ExecutorManager {
    private var isRunning = false
    private var job: Job? = null
    private var ocrProcessor: OCRProcessor? = null
    private var debugOverlay: OCRDebugOverlay? = null

    fun start(service: AccessibilityService) {
        if (isRunning) {
            Log.d("Executor", "이미 실행 중입니다.")
            Toast.makeText(service, "이미 실행 중입니다", Toast.LENGTH_SHORT).show()
            return
        }

        isRunning = true
        Log.d("Executor", "✅ 자동화 루틴 시작됨.")
        Toast.makeText(service, "✅ 자동화 시작됨", Toast.LENGTH_SHORT).show()

        ocrProcessor = OCRProcessor().apply { init(service) }
        debugOverlay = OCRDebugOverlay(service.applicationContext)
        val click = ClickSimulator(service)

        job = CoroutineScope(Dispatchers.Default).launch {
            while (isRunning) {
                delay(2000)

                val triggerCoordinate = CoordinateManager.getPrimaryCoordinate()
                if (triggerCoordinate == null) {
                    Log.e("Executor", "[❌] 트리거 좌표가 없습니다.")
                    continue
                }

                val bitmap = OCRCaptureUtils.captureRegion(triggerCoordinate)
                if (bitmap == null) {
                    Log.e("Executor", "[❌] OCR 캡처 실패")
                    continue
                }

                val text = ocrProcessor?.getText(bitmap)?.trim() ?: ""
                val triggerValue = text.toDoubleOrNull()?.toInt() ?: 0

                Log.d("Executor", "[🔍] OCR 텍스트: \"$text\"")
                Log.d("Executor", "[🔍] 파싱된 숫자: $triggerValue")

                withContext(Dispatchers.Main) {
                    val statusText = if (triggerValue >= 1) "✅ 조건 충족" else "⏸ 조건 미달"
                    debugOverlay?.show(triggerCoordinate.toRect(), "$text\n$statusText")

                    val toastMsg = if (triggerValue >= 1) {
                        "✅ 조건 만족! 루틴 실행"
                    } else {
                        "⏸ 조건 미달 ($triggerValue)"
                    }
                    Toast.makeText(service, toastMsg, Toast.LENGTH_SHORT).show()
                }

                if (triggerValue >= 1) {
                    Log.d("Executor", "[✅] 조건 만족! → 루틴 실행 시작.")
                    executeStepFlow(click)
                } else {
                    Log.d("Executor", "[⏸] 조건 불충족 → $triggerValue < 1 → 대기 중.")
                }
            }
        }
    }

    fun stop() {
        isRunning = false
        job?.cancel()
        debugOverlay?.dismiss()
        job = null
        Log.d("Executor", "⛔ 자동화 루틴 정지됨.")
    }

    fun getIsRunning(): Boolean = isRunning

    private suspend fun executeStepFlow(click: ClickSimulator) {
        Log.d("Executor", "[▶️] 루틴 동작 중... (실제 클릭 루틴 진입)")
        delay(1000)
        // 여기서 자동화 클릭 실행 로직이 들어감
    }
}
