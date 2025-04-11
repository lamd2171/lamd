package com.example.bitconintauto.service

import android.content.Context
import android.util.Log
import com.example.bitconintauto.model.Coordinate
import com.example.bitconintauto.ocr.OCRProcessor
import com.example.bitconintauto.util.CoordinateManager
import com.example.bitconintauto.util.OCRCaptureUtils
import com.example.bitconintauto.util.PreferenceHelper
import com.example.bitconintauto.util.ValueChangeDetector
import kotlinx.coroutines.*

object ExecutorManager {

    private var internalIsRunning = false

    val isRunning: Boolean
        get() = internalIsRunning

    private var job: Job? = null
    private var lastValue: Double? = null
    private const val intervalMillis: Long = 2000L

    fun start(context: Context) {
        if (internalIsRunning) return
        internalIsRunning = true

        val service = PreferenceHelper.accessibilityService
        if (service == null) {
            Log.e("ExecutorManager", "AccessibilityService not available")
            return
        }

        val autoClicker = AutoClicker(service)
        val ocrProcessor = OCRProcessor().apply { init(context) }

        job = CoroutineScope(Dispatchers.Default).launch {
            while (internalIsRunning) {
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
        internalIsRunning = false
        job?.cancel()
        job = null
    }
}
