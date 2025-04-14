package com.example.bitconintauto.service

import android.accessibilityservice.AccessibilityService
import com.example.bitconintauto.model.Coordinate
import com.example.bitconintauto.ocr.OCRProcessor
import com.example.bitconintauto.ui.OCRDebugOverlay
import com.example.bitconintauto.util.*
import kotlinx.coroutines.*
import android.util.Log

object ExecutorManager {
    private var isRunning = false
    private var job: Job? = null
    private var ocrProcessor: OCRProcessor? = null
    private var debugOverlay: OCRDebugOverlay? = null

    fun start(service: AccessibilityService) {
        if (isRunning) return
        isRunning = true

        ocrProcessor = OCRProcessor().apply { init(service) }
        debugOverlay = OCRDebugOverlay(service.applicationContext)
        val click = ClickSimulator(service)

        job = CoroutineScope(Dispatchers.Default).launch {
            while (isRunning) {
                delay(2000)

                val triggerCoordinate = CoordinateManager.getPrimaryCoordinate()
                if (triggerCoordinate == null) {
                    Log.e("ExecutorManager", "trigger 좌표 없음")
                    continue
                }

                val bitmap = OCRCaptureUtils.captureRegion(triggerCoordinate)
                val text = bitmap?.let { ocrProcessor?.getText(it) } ?: ""
                val triggerValue = text.trim().toDoubleOrNull()?.toInt() ?: 0

                withContext(Dispatchers.Main) {
                    debugOverlay?.show(triggerCoordinate.toRect(), text)
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

    fun getIsRunning(): Boolean = isRunning

    private suspend fun executeStepFlow(click: ClickSimulator) {
        val sequence = listOf("step2", "step3", "step4", "step5", "step6", "step7", "step8", "step9", "step10")
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
}
