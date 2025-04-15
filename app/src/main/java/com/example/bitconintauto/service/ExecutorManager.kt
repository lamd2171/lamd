package com.example.bitconintauto.service

import android.accessibilityservice.AccessibilityService
import android.graphics.Rect
import android.util.Log
import android.widget.TextView
import com.example.bitconintauto.model.Coordinate
import com.example.bitconintauto.ocr.OCRProcessor
import com.example.bitconintauto.ui.OverlayView
import com.example.bitconintauto.util.*
import kotlinx.coroutines.*

object ExecutorManager {
    private var isRunning = false
    private var job: Job? = null

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

                            CoroutineScope(Dispatchers.Default).launch {
                                executeRoutine(clicker, ocr, overlayView)
                            }
                        }
                    }
                }
            }
        }
    }

    fun stop() {
        isRunning = false
        job?.cancel()
    }

    private suspend fun executeRoutine(clicker: ClickSimulator, ocr: OCRProcessor, overlayView: TextView) {
        delay(600)

        val steps = listOf(
            Pair("step2", "Send"),
            Pair("step3", "Address Book"),
            Pair("step4", "User"),
            Pair("step5", "Next"),
            Pair("step6", "Max"),
            Pair("step7", "Next"),
            Pair("step8", "Send"),
            Pair("step9", "BTCT Status"),
            Pair("step10", "My BTCT")
        )

        for ((label, keyword) in steps) {
            val coord = CoordinateManager.get(label).firstOrNull() ?: continue
            val text = clicker.readText(coord)
            if (text.contains(keyword, true)) {
                clicker.performClick(coord)
                delay(600)
            }
        }

        clicker.scrollUntilVisible("step11", "scrollArea")
        delay(500)

        val copyCoord = CoordinateManager.get("step13").firstOrNull() ?: return
        clicker.performClick(copyCoord)
        delay(300)

        val valueText = clicker.readText(copyCoord)
        val value = valueText.toDoubleOrNull() ?: return
        val result = "%.6f".format(value + 0.001)

        val pasteCoord = CoordinateManager.get("step20").firstOrNull() ?: return
        clicker.clearAndInput(pasteCoord, result)
        delay(500)

        val balance = "step21"
        val target = "step21Check"
        var inputValue = result.toDoubleOrNull() ?: return

        repeat(10) {
            clicker.clearAndInput(pasteCoord, "%.6f".format(inputValue))
            delay(400)
            if (clicker.isValueMatched(balance, target)) return@repeat
            inputValue += 0.001
        }

        clicker.performClick(CoordinateManager.get("step22").firstOrNull() ?: return)
        delay(500)

        clicker.scrollUntilVisible("step23", "scrollArea")
        delay(400)

        listOf("step24", "step25", "step26").forEach {
            val coord = CoordinateManager.get(it).firstOrNull() ?: return@forEach
            clicker.performClick(coord)
            delay(500)
        }

        Log.d("Executor", "✅ 루틴 종료")
    }
}
