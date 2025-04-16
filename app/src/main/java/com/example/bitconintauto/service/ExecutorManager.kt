package com.example.bitconintauto.service

import android.content.Context
import android.util.Log
import com.example.bitconintauto.model.Coordinate
import com.example.bitconintauto.ocr.OCRProcessor
import com.example.bitconintauto.ui.OverlayView
import com.example.bitconintauto.util.*
import kotlinx.coroutines.*

object ExecutorManager {
    private var job: Job? = null
    private var isRunning = false

    fun start(context: Context) {
        if (isRunning) return
        isRunning = true

        val ocr = OCRProcessor()
        val triggerCoord = Coordinate(45, 240, 350, 120, "trigger")

        job = CoroutineScope(Dispatchers.Default).launch {
            while (isRunning) {
                delay(1500)

                val bmp = ScreenCaptureHelper.captureSync() ?: continue
                val region = OCRCaptureUtils.captureRegion(bmp, triggerCoord)
                val value = ocr.getText(region ?: continue)
                val valueInt = value.trim().toDoubleOrNull()?.toInt() ?: 0

                withContext(Dispatchers.Main) {
                    OverlayView.updateText("Trigger → $valueInt")
                    if (PreferenceHelper.debugMode) {
                        OverlayView.drawDebugBox(triggerCoord, value)
                    }
                }

                if (valueInt >= 1) {
                    Log.d("Executor", "✅ Trigger 감지 → $valueInt")
                    executeRoutine(context, ocr)
                }
            }
        }
    }

    private suspend fun executeRoutine(context: Context, ocr: OCRProcessor) {
        val service = PreferenceHelper.accessibilityService ?: return
        val clicker = ClickSimulator(service)

        val labelList = listOf(
            "step2" to "Send", "step3" to "Address Book", "step4" to "User", "step5" to "Next",
            "step6" to "Max", "step7" to "Next", "step8" to "Send", "step9" to "BTCT Status"
        )

        for ((label, keyword) in labelList) {
            val coord = CoordinateManager.get(label).firstOrNull() ?: continue
            val result = clicker.readText(coord)
            withContext(Dispatchers.Main) {
                OverlayView.drawDebugBox(coord, result)
            }
            if (result.contains(keyword, true)) {
                clicker.performClick(coord)
                delay(700)
            }
        }

        clicker.scrollUntilVisible("step11", "scrollArea")
        delay(500)

        val copyCoord = CoordinateManager.get("step13").firstOrNull() ?: return
        clicker.performClick(copyCoord)
        delay(300)
        val valueText = clicker.readText(copyCoord)
        val value = valueText.toDoubleOrNull() ?: return
        val resultText = "%.6f".format(value + 0.001)

        val pasteCoord = CoordinateManager.get("step20").firstOrNull() ?: return
        clicker.clearAndInput(pasteCoord, resultText)
        delay(500)

        val balanceLabel = "step21"
        val targetLabel = "step21Check"
        var inputValue = resultText.toDoubleOrNull() ?: return

        repeat(10) {
            clicker.clearAndInput(pasteCoord, "%.6f".format(inputValue))
            delay(500)
            if (clicker.isValueMatched(balanceLabel, targetLabel)) return@repeat
            inputValue += 0.001
        }

        CoordinateManager.get("step22").firstOrNull()?.let {
            clicker.performClick(it)
            delay(500)
        }

        clicker.scrollUntilVisible("step23", "scrollArea")
        delay(500)

        listOf("step24", "step25", "step26").forEach { label ->
            CoordinateManager.get(label).firstOrNull()?.let {
                clicker.performClick(it)
                delay(500)
            }
        }

        withContext(Dispatchers.Main) {
            OverlayView.updateText("✅ 루틴 종료 → Trigger 대기")
        }
    }

    fun stop() {
        isRunning = false
        job?.cancel()
    }
}
