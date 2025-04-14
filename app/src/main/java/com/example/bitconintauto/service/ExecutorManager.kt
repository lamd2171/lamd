// app/src/main/java/com/example/bitconintauto/service/ExecutorManager.kt
package com.example.bitconintauto.service

import android.content.Context
import android.graphics.Rect
import android.util.Log
import com.example.bitconintauto.model.Coordinate
import com.example.bitconintauto.ocr.OCRProcessor
import com.example.bitconintauto.ui.OCRDebugOverlay
import com.example.bitconintauto.util.ClickSimulator
import com.example.bitconintauto.util.CoordinateManager
import com.example.bitconintauto.util.OCRCaptureUtils
import kotlinx.coroutines.*

object ExecutorManager {
    private var isRunning = false
    private var job: Job? = null
    private var ocrProcessor: OCRProcessor? = null
    private var debugOverlay: OCRDebugOverlay? = null

    fun start(context: Context) {
        if (isRunning) return
        isRunning = true

        ocrProcessor = OCRProcessor().apply { init(service) }
        debugOverlay = OCRDebugOverlay(service)
        val click = ClickSimulator(service)

        job = CoroutineScope(Dispatchers.Default).launch {
            while (isRunning) {
                delay(2000)

                val triggerCoordinate = CoordinateManager.get("trigger").firstOrNull()
                if (triggerCoordinate == null) continue

                val bitmap = OCRCaptureUtils.captureRegion(triggerCoordinate)
                val text = bitmap?.let { ocrProcessor?.getText(it) } ?: ""
                val triggerValue = text.trim().toDoubleOrNull()?.toInt() ?: 0

                withContext(Dispatchers.Main) {
                    private fun Coordinate.toRect(): Rect {
                        return Rect(x, y, x + width, y + height)
                    }
                }

                if (triggerValue >= 1) {
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

    private fun Coordinate.toRect(): Rect {
        return Rect(x, y, x + width, y + height)
    }

    fun getIsRunning(): Boolean = isRunning

    private suspend fun executeStepFlow(click: ClickSimulator) {
        val sequence = listOf(
            "step2", "step3", "step4", "step5", "step6",
            "step7", "step8", "step9", "step10"
        )
        for (step in sequence) {
            click.performClick(step)
            delay(1000)
        }

        click.scrollToBottom("btctScrollArea")
        delay(1500)

        val step12 = CoordinateManager.get("step12").firstOrNull()
        val bitmap = step12?.let { OCRCaptureUtils.captureRegion(it) }
        var rawPrice = bitmap?.let { ocrProcessor?.getText(it) } ?: "0"
        var price = (rawPrice.trim().toDoubleOrNull() ?: 0.0) + 0.001

        click.performClick("step14")
        delay(500)
        click.performClick("step15")
        delay(500)
        click.performClick("step16")
        delay(1000)
        click.scrollTo("exchangeScrollArea", "sellTabVisible")
        delay(1000)

        click.clearAndInput("orderPriceField", String.format("%.3f", price))
        delay(500)

        var attempts = 0
        while (!click.isValueMatched("sellableValue", "balanceValue") && attempts < 5) {
            price += 0.001
            click.clearAndInput("orderPriceField", String.format("%.3f", price))
            delay(500)
            attempts++
        }

        click.performClick("stepQtyMax")
        delay(500)
        click.performClick("stepSell")
        delay(1000)
        click.performClick("stepSellConfirm")
        delay(1000)
        click.performClick("stepPay")
        delay(1000)
    }

    private fun com.example.bitconintauto.model.Coordinate.toRect(): Rect {
        return Rect(x, y, x + width, y + height)
    }
}
