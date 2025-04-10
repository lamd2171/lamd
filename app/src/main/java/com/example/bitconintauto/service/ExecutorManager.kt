package com.example.bitconintauto.logic

import android.content.Context
import android.util.Log
import com.example.bitconintauto.model.Coordinate
import com.example.bitconintauto.service.AutoClicker
import com.example.bitconintauto.ocr.OCRProcessor
import com.example.bitconintauto.util.CoordinateManager
import com.example.bitconintauto.util.OCRCaptureUtils
import com.example.bitconintauto.util.PreferenceHelper
import kotlinx.coroutines.*

object ExecutorManager {

    private var isRunning = false
    private var job: Job? = null
    private var lastValue: Double? = null
    private const val intervalMillis: Long = 2000L

    fun start(context: Context) {
        if (isRunning) return
        isRunning = true

        val service = PreferenceHelper.accessibilityService ?: run {
            Log.e("ExecutorManager", "AccessibilityService not available")
            return
        }

        val autoClicker = AutoClicker(service)
        val ocrProcessor = OCRProcessor().apply { init(context) }

        job = CoroutineScope(Dispatchers.Default).launch {
            while (isRunning) {
                val primaryCoord: Coordinate = CoordinateManager.getPrimaryCoordinate() ?: continue
                val bitmap = OCRCaptureUtils.capture(service, primaryCoord)
                val currentValue = bitmap?.let { ocrProcessor.getText(it).toDoubleOrNull() }

                if (ValueChangeDetector.hasSignificantChange(lastValue, currentValue, CoordinateManager.getThreshold())) {
                    Log.d("ExecutorManager", "Value changed: $lastValue → $currentValue")
                    lastValue = currentValue

                    withContext(Dispatchers.Main) {
                        autoClicker.executeCycle(
                            CoordinateManager.getClickPathSequence(),
                            CoordinateManager.getCopyTarget(),
                            CoordinateManager.getUserOffset(),
                            CoordinateManager.getPasteTarget()
                        )
                    }
                }

                delay(intervalMillis)
            }
        }
    }

    fun stop() {
        isRunning = false
        job?.cancel()
        job = null
    }
}
