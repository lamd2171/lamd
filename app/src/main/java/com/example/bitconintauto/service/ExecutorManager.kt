package com.example.bitconintauto.service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.widget.TextView
import com.example.bitconintauto.model.Coordinate
import com.example.bitconintauto.ocr.OCRProcessor
import com.example.bitconintauto.ui.OverlayView
import com.example.bitconintauto.util.*
import kotlinx.coroutines.*

object ExecutorManager {
    private var job: Job? = null
    private var isRunning = false

    fun start(service: AccessibilityService, overlayView: TextView) {
        if (isRunning) return
        isRunning = true

        val ocr = OCRProcessor()
        val clicker = ClickSimulator(service)
        val triggerCoord = Coordinate(45, 240, 350, 120, "trigger")

        job = CoroutineScope(Dispatchers.Default).launch {
            while (isRunning) {
                delay(1500)

                ScreenCaptureHelper.capture { bmp ->
                    if (bmp != null) {
                        val region = OCRCaptureUtils.captureRegion(bmp, triggerCoord)
                        val value = ocr.getText(region)
                        val valueInt = value.trim().toDoubleOrNull()?.toInt() ?: 0

                        CoroutineScope(Dispatchers.Main).launch {
                            overlayView.text = "Trigger → $valueInt"
                        }

                        if (valueInt >= 1) {
                            Log.d("Executor", "✅ Trigger 감지 → $valueInt")
                            executeRoutine(clicker, ocr, overlayView)
                        }
                    }
                }
            }
        }
    }

    private fun executeRoutine(clicker: ClickSimulator, ocr: OCRProcessor, overlayView: TextView) {
        CoroutineScope(Dispatchers.Default).launch {
            val labelList = listOf(
                "step2" to "Send", "step3" to "Address Book", "step4" to "User", "step5" to "Next",
                "step6" to "Max", "step7" to "Next", "step8" to "Send", "step9" to "BTCT Status"
            )

            for ((label, keyword) in labelList) {
                val coord = CoordinateManager.get(label).firstOrNull() ?: continue
                val result = clicker.readText(coord)
                if (result.contains(keyword, true)) {
                    clicker.performClick(coord)
                    delay(700)
                }
            }

            clicker.scrollUntilVisible("step11", "scrollArea")
            delay(500)

            val copyCoord = CoordinateManager.get("step13").firstOrNull() ?: return@launch
            clicker.performClick(copyCoord)
            delay(300)
            val valueText = clicker.readText(copyCoord)
            val value = valueText.toDoubleOrNull() ?: return@launch
            val resultText = "%.6f".format(value + 0.001)

            val pasteCoord = CoordinateManager.get("step20").firstOrNull() ?: return@launch
            clicker.clearAndInput(pasteCoord, resultText)
            delay(500)

            val balanceLabel = "step21"
            val targetLabel = "step21Check"
            var inputValue = resultText.toDoubleOrNull() ?: return@launch

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

            CoroutineScope(Dispatchers.Main).launch {
                overlayView.text = "✅ 루틴 종료 → Trigger 대기"
            }
        }
    }

    fun stop() {
        isRunning = false
        job?.cancel()
    }
}
