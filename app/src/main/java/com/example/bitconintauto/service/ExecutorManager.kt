package com.example.bitconintauto.service

import android.content.Context
import android.util.Log
import com.example.bitconintauto.model.Coordinate
import com.example.bitconintauto.ocr.OCRProcessor
import com.example.bitconintauto.util.*
import kotlinx.coroutines.*

object ExecutorManager {

    val isRunning: Boolean
        get() = job?.isActive == true

    private var job: Job? = null
    private const val intervalMillis: Long = 2000L

    fun start(context: Context) {
        if (isRunning) return

        val service = PreferenceHelper.accessibilityService
        if (service == null) {
            Log.e("ExecutorManager", "AccessibilityService not available")
            return
        }

        val autoClicker = AutoClicker(service)
        val ocrProcessor = OCRProcessor().apply { init(context) }

        job = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                val primaryCoord = CoordinateManager.getPrimaryCoordinate() ?: continue
                val bitmap = OCRCaptureUtils.capture(service, primaryCoord)
                val currentValue = bitmap?.let { ocrProcessor.getText(it).toDoubleOrNull() }

                val expectedRaw = primaryCoord.expectedValue
                val matched = expectedRaw?.let {
                    ComparisonUtils.matchCondition(currentValue ?: 0.0, it)
                } ?: false

                if (matched) {
                    Log.d("ExecutorManager", "OCR 조건 만족: $currentValue matches $expectedRaw")

                    withContext(Dispatchers.Main) {
                        autoClicker.executeCycle(
                            CoordinateManager.getClickPathSequence(),
                            CoordinateManager.getCopyTarget(),
                            CoordinateManager.getUserOffset(),
                            CoordinateManager.getPasteTarget()
                        )
                    }
                } else {
                    Log.d("ExecutorManager", "OCR 조건 불일치: $currentValue vs 조건 $expectedRaw")
                }

                delay(intervalMillis)
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }
}
