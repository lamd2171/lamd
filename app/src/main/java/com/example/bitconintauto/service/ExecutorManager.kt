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
    private var lastValue: Double? = null
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
        job?.cancel()
        job = null
    }
}
