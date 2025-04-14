package com.example.bitconintauto.service

import android.accessibilityservice.AccessibilityService
import android.graphics.Rect
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
            Toast.makeText(service, "이미 실행 중입니다", Toast.LENGTH_SHORT).show()
            return
        }
        isRunning = true
        Toast.makeText(service, "✅ 자동화 시작됨", Toast.LENGTH_SHORT).show()

        // 트리거 기본 등록
        CoordinateManager.set(
            "trigger", Coordinate(x = 45, y = 240, width = 350, height = 120, label = "trigger")
        )

        ocrProcessor = OCRProcessor().apply { init(service) }
        debugOverlay = OCRDebugOverlay(service.applicationContext)
        val click = ClickSimulator(service)

        job = CoroutineScope(Dispatchers.Default).launch {
            while (isRunning) {
                delay(2000)

                val trigger = CoordinateManager.getPrimaryCoordinate() ?: run {
                    Log.e("Executor", "[❌] 트리거 좌표가 없습니다.")
                    return@launch
                }

                val bitmap = OCRCaptureUtils.capture(service, trigger) ?: continue
                val text = ocrProcessor?.getText(bitmap)?.trim() ?: ""
                val triggerValue = text.toDoubleOrNull() ?: 0.0

                withContext(Dispatchers.Main) {
                    val status = if (triggerValue >= 1.0) "✅ 조건 충족" else "⏸ 조건 미달"
                    debugOverlay?.show(trigger.toRect(), "trigger\n$text\n$status")
                }

                if (triggerValue >= 1.0) {
                    Log.d("Executor", "[✅ Trigger 감지: $text] 루틴 실행")
                    executeStepFlow(click)
                } else {
                    Log.d("Executor", "[⏸ Trigger 미충족: $text]")
                }
            }
        }
    }

    fun stop() {
        isRunning = false
        job?.cancel()
        debugOverlay?.dismiss()
    }

    fun getIsRunning(): Boolean = isRunning

    private suspend fun executeStepFlow(click: ClickSimulator) {
        delay(500)

        // step2~step10 OCR 텍스트 기반 클릭
        val ocrSteps = mapOf(
            "step2" to "Send",
            "step3" to "Address Book",
            "step4" to "User",
            "step5" to "Next",
            "step6" to "Max",
            "step7" to "Next",
            "step8" to "Send",
            "step9" to "BTCT Status",
            "step10" to "My BTCT"
        )

        for ((label, keyword) in ocrSteps) {
            click.clickIfTextFound(label, keyword)
            delay(700)
        }

        click.scrollUntilVisible("step11", "scrollArea")
        delay(500)

        CoordinateManager.get("step12").firstOrNull()?.let {
            click.performClick(it)
        }
        delay(300)

        val step13 = CoordinateManager.get("step13").firstOrNull() ?: return
        click.performClick(step13)
        delay(300)
        val valueText = click.readText(step13)
        val value = valueText.toDoubleOrNull() ?: return
        val resultText = "%.6f".format(value + 0.001)

        CoordinateManager.get("step14").firstOrNull()?.let {
            click.performClick(it)
        }
        delay(300)

        listOf("step15", "step16", "step17").forEach { label ->
            CoordinateManager.get(label).firstOrNull()?.let {
                click.performClick(it)
                delay(500)
            }
        }

        click.scrollUntilVisible("step18", "scrollArea")
        delay(500)

        CoordinateManager.get("step19").firstOrNull()?.let {
            click.performClick(it)
        }
        delay(300)

        val pasteTarget = CoordinateManager.get("step20").firstOrNull() ?: return
        click.clearAndInput(pasteTarget.label, resultText)
        delay(500)

        val balanceLabel = "step21"
        val targetLabel = "step21Check"
        var inputValue = resultText.toDoubleOrNull() ?: return
        repeat(10) {
            click.clearAndInput(pasteTarget.label, "%.6f".format(inputValue))
            delay(500)
            val matched = click.isValueMatched(balanceLabel, targetLabel)
            if (matched) return@repeat
            inputValue += 0.001
        }

        CoordinateManager.get("step22").firstOrNull()?.let {
            click.performClick(it)
        }
        delay(500)

        click.scrollUntilVisible("step23", "scrollArea")
        delay(500)

        listOf("step24", "step25", "step26").forEach { label ->
            CoordinateManager.get(label).firstOrNull()?.let {
                click.performClick(it)
                delay(500)
            }
        }

        Log.d("Executor", "[🏁 루틴 종료] 트리거 대기 중...")
    }

    // 확장용 전체 스크린 캡처 디버깅 (선택적)
    private fun showDebugFullScreen(service: AccessibilityService) {
        val bmp = OCRCaptureUtils.captureFullScreen(service)
        bmp?.let {
            val bounds = Rect(0, 0, it.width, it.height)
            debugOverlay?.show(bounds, "전체 화면 캡처")
        }
    }
}
