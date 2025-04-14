// ✅ ExecutorManager.kt - 트리거 영역 유지 + step2~26 순서 보장 전체 흐름 (OCR 포함)

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
            Toast.makeText(service, "이미 실행 중입니다", Toast.LENGTH_SHORT).show()
            return
        }

        isRunning = true
        Toast.makeText(service, "✅ 자동화 시작됨", Toast.LENGTH_SHORT).show()

        CoordinateManager.set("trigger", Coordinate(x = 80, y = 220, width = 500, height = 100, label = "trigger"))

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
                val lines = text.split("\n")
                val lastLineDigits = lines.lastOrNull()?.replace("[^0-9.]".toRegex(), "") ?: ""
                val triggerRaw = lastLineDigits.toDoubleOrNull() ?: 0.0

                withContext(Dispatchers.Main) {
                    val status = if (triggerRaw >= 1.0) "✅ 조건 충족" else "⏸ 조건 미달"
                    debugOverlay?.show(trigger.toRect(), "trigger\n$text\n$status")
                }

                if (triggerRaw >= 1.0) {
                    Log.d("Executor", "[✅ Trigger 감지: $text → 추출: $triggerRaw] 루틴 실행")
                    executeStepFlow(click)
                }

            }
        }
    }

    fun stop() {
        isRunning = false
        job?.cancel()
        debugOverlay?.dismiss()
        job = null
    }

    fun getIsRunning(): Boolean = isRunning

    private suspend fun executeStepFlow(click: ClickSimulator) {
        delay(500)

        val stepLabels = listOf(
            "step2","step3","step4","step5","step6","step7","step8",
            "step9","step10"
        )

        for (label in stepLabels) {
            Log.d("Executor", "[▶️] Step 실행: $label")
            val coord = CoordinateManager.get(label).firstOrNull() ?: continue
            click.performClick(coord)
            delay(600)
        }

        click.scrollUntilVisible("step11", "scrollArea")
        delay(500)

        val step12 = CoordinateManager.get("step12").firstOrNull()
        if (step12 != null) {
            Log.d("Executor", "[▶️] Step 실행: step12")
            click.performClick(step12)
            delay(300)
        }

        val step13 = CoordinateManager.get("step13").firstOrNull() ?: run {
            Log.e("Executor", "[❌] step13 좌표가 없습니다.")
            return
        }
        Log.d("Executor", "[▶️] Step 실행: step13")
        click.performClick(step13)
        delay(300)
        val valueText = click.readText(step13)
        val value = valueText.toDoubleOrNull() ?: return
        val resultText = "%.6f".format(value + 0.001)

        val step14 = CoordinateManager.get("step14").firstOrNull()
        if (step14 != null) {
            Log.d("Executor", "[▶️] Step 실행: step14")
            click.performClick(step14)
            delay(300)
        }

        val pasteTarget = CoordinateManager.get("step20").firstOrNull() ?: return
        Log.d("Executor", "[▶️] Step 실행: step20 (입력)")
        click.clearAndInput(pasteTarget.label, resultText)
        delay(500)

        val finalSteps = listOf("step15","step16","step17")
        for (label in finalSteps) {
            Log.d("Executor", "[▶️] Step 실행: $label")
            val coord = CoordinateManager.get(label).firstOrNull() ?: continue
            click.performClick(coord)
            delay(600)
        }

        Log.d("Executor", "[▶️] Step 실행: step18 (스크롤)")
        click.scrollUntilVisible("step18", "scrollArea")

        Log.d("Executor", "[▶️] Step 실행: step19")
        click.performClick(CoordinateManager.get("step19").firstOrNull() ?: return)

        val qtyTarget = CoordinateManager.get("step22").firstOrNull() ?: return
        val balanceLabel = "step21"
        val targetLabel = "step21Check"

        var inputValue = resultText.toDoubleOrNull() ?: return
        repeat(10) {
            Log.d("Executor", "[🔁] Step 실행: step21 (비교 시도) $it")
            click.clearAndInput(pasteTarget.label, "%.6f".format(inputValue))
            delay(500)
            val matched = click.isValueMatched(balanceLabel, targetLabel)
            if (matched) return@repeat else inputValue += 0.001
        }

        Log.d("Executor", "[▶️] Step 실행: step22")
        click.performClick(qtyTarget)

        Log.d("Executor", "[▶️] Step 실행: step23 (스크롤)")
        click.scrollUntilVisible("step23", "scrollArea")

        val endSteps = listOf("step24","step25","step26")
        for (label in endSteps) {
            Log.d("Executor", "[▶️] Step 실행: $label")
            val coord = CoordinateManager.get(label).firstOrNull() ?: continue
            click.performClick(coord)
            delay(600)
        }

        Log.d("Executor", "[🏁] 루틴 종료. Trigger 대기")
    }
}