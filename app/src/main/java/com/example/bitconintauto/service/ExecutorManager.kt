// [15] app/src/main/java/com/example/bitconintauto/service/ExecutorManager.kt

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

        CoordinateManager.set("trigger", Coordinate(45, 240, 350, 120, "trigger"))

        ocrProcessor = OCRProcessor().apply { init(service.applicationContext) }
        debugOverlay = OCRDebugOverlay(service.applicationContext)
        val click = ClickSimulator(service)

        job = CoroutineScope(Dispatchers.Default).launch {
            while (isRunning) {
                delay(2000)
                val trigger = CoordinateManager.getPrimaryCoordinate() ?: continue

                var text = ""
                ScreenCaptureHelper.capture { fullBitmap ->
                    if (fullBitmap != null) {
                        val targetBitmap = OCRCaptureUtils.captureRegion(fullBitmap, trigger)
                        text = ocrProcessor?.getText(targetBitmap)?.trim() ?: ""
                    }
                }
                delay(300)

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
        ocrProcessor?.release()
    }

    fun getIsRunning(): Boolean = isRunning

    private suspend fun executeStepFlow(click: ClickSimulator) {
        delay(500)

        val stepMap = mapOf(
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

        for ((label, keyword) in stepMap) {
            val coord = CoordinateManager.get(label).firstOrNull() ?: continue
            val result = click.readText(coord)
            Log.d("Executor", "[🔍 OCR Step] $label → \"$result\" vs \"$keyword\"")
            if (result.contains(keyword, true)) {
                click.performClick(coord)
                delay(700)
            }
        }

        click.scrollUntilVisible("step11", "scrollArea")
        delay(500)

        val copyCoord = CoordinateManager.get("step13").firstOrNull() ?: return
        click.performClick(copyCoord)
        delay(300)
        val valueText = click.readText(copyCoord)
        val value = valueText.toDoubleOrNull() ?: return
        val resultText = "%.6f".format(value + 0.001)

        val pasteCoord = CoordinateManager.get("step20").firstOrNull() ?: return
        click.clearAndInput(pasteCoord, resultText)
        delay(500)

        val balanceLabel = "step21"
        val targetLabel = "step21Check"
        var inputValue = resultText.toDoubleOrNull() ?: return

        repeat(10) {
            click.clearAndInput(pasteCoord, "%.6f".format(inputValue))
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