// ✅ ExecutorManager.kt - 트리거 인식 → OCR 기반 step2~step26 자동화 수행

package com.example.bitconintauto.service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.widget.Toast
import com.example.bitconintauto.ocr.OCRProcessor
import com.example.bitconintauto.ui.OCRDebugOverlay
import com.example.bitconintauto.util.*
import kotlinx.coroutines.*
import com.example.bitconintauto.model.Coordinate

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

        CoordinateManager.set(
            "trigger", Coordinate(
                x = 45, y = 240, width = 350, height = 120, label = "trigger"
            )
        )

        ocrProcessor = OCRProcessor().apply { init(service) }
        debugOverlay = OCRDebugOverlay(service.applicationContext)
        val click = ClickSimulator(service)

        job = CoroutineScope(Dispatchers.Default).launch {
            while (isRunning) {
                delay(2000)
                val trigger = CoordinateManager.getPrimaryCoordinate() ?: continue
                val bitmap = OCRCaptureUtils.capture(service, trigger) ?: continue
                val text = ocrProcessor?.getText(bitmap)?.trim() ?: continue
                val triggerValue = text.toDoubleOrNull()?.toInt() ?: 0

                withContext(Dispatchers.Main) {
                    val status = if (triggerValue >= 1) "✅ 조건 충족" else "⏸ 조건 미달"
                    debugOverlay?.show(trigger.toRect(), "trigger\n$text\n$status")
                }

                if (triggerValue >= 1) {
                    Log.d("Executor", "[✅ Trigger 감지: $text] 루틴 실행")
                    executeStepFlow(click)
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
            val coord = CoordinateManager.get(label).firstOrNull() ?: continue
            val bmp = OCRCaptureUtils.capture(click.service, coord) ?: continue
            val ocrText = OCRProcessor().getText(bmp).trim()

            Log.d("Executor", "[🔍 OCR Step] $label → \"$ocrText\" vs \"$keyword\"")
            if (ocrText.contains(keyword, true)) {
                click.performClick(coord)
                Log.d("Executor", "[✅ 클릭] $label ($keyword)")
                delay(700)
            } else {
                Log.w("Executor", "[❌ 미일치] $label (텍스트: $ocrText)")
            }
        }

        click.scrollUntilVisible("step11", "scrollArea")
        delay(500)

        val step12 = CoordinateManager.get("step12").firstOrNull()
        step12?.let {
            click.performClick(it)
            delay(300)
        }

        val step13 = CoordinateManager.get("step13").firstOrNull() ?: return
        click.performClick(step13)
        delay(300)
        val valueText = click.readText(step13)
        val value = valueText.toDoubleOrNull() ?: return
        val resultText = "%.6f".format(value + 0.001)

        val step14 = CoordinateManager.get("step14").firstOrNull()
        step14?.let {
            click.performClick(it)
            delay(300)
        }

        listOf("step15", "step16", "step17").forEach { label ->
            CoordinateManager.get(label).firstOrNull()?.let {
                click.performClick(it)
                delay(500)
            }
        }

        click.scrollUntilVisible("step18", "scrollArea")
        delay(500)

        val step19 = CoordinateManager.get("step19").firstOrNull()
        step19?.let {
            click.performClick(it)
            delay(300)
        }

        val pasteTarget = CoordinateManager.get("step20").firstOrNull() ?: return
        click.clearAndInput(pasteTarget.label, resultText)
        delay(500)

        val balanceLabel = "step21"
        val targetLabel = "step21Check"
        var inputValue = resultText.toDoubleOrNull() ?: return
        repeat(10) {
            click.clearAndInput(pasteTarget.label, "%.6f".format(inputValue))
            delay(500)
            if (click.isValueMatched(balanceLabel, targetLabel)) return@repeat
            inputValue += 0.001
        }

        CoordinateManager.get("step22").firstOrNull()?.let {
            click.performClick(it)
            delay(500)
        }

        click.scrollUntilVisible("step23", "scrollArea")
        delay(500)

        listOf("step24", "step25", "step26").forEach { label ->
            CoordinateManager.get(label).firstOrNull()?.let {
                click.performClick(it)
                delay(500)
            }
        }

        Log.d("Executor", "[🏁 루틴 종료] 트리거 감지 대기 중...")
    }
}