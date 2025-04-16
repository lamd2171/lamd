package com.example.bitconintauto.service

import android.content.Context
import android.util.Log
import com.example.bitconintauto.model.Coordinate
import com.example.bitconintauto.ui.OverlayView
import com.example.bitconintauto.util.CoordinateManager
import com.example.bitconintauto.util.OCRCaptureUtils
import kotlinx.coroutines.*

object ExecutorManager {
    private var job: Job? = null

    fun start(context: Context, overlayView: OverlayView) {
        if (job != null) return

        job = CoroutineScope(Dispatchers.Default).launch {
            Log.d("Executor", "▶▶ 루프 진입 시작됨")
            while (isActive) {
                Log.d("Executor", "🌀 루프 실행 중")
                delay(1000)

                val bitmap = OCRCaptureUtils.captureSync()
                if (bitmap == null) {
                    Log.d("Executor", "⚠️ 캡처 실패: bitmap == null")
                    continue
                }

                val coordinates = CoordinateManager.getAllCoordinates()
                for (coordinate in coordinates) {
                    val ocrResult = OCRCaptureUtils.recognizeNumber(bitmap)
                    Log.d("Executor", "🔎 OCR 결과: $ocrResult")
                    overlayView.updateDebugText("${coordinate.label} → $ocrResult")
                }
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }
}
