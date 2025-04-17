package com.example.bitconintauto.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.bitconintauto.model.Coordinate
import com.example.bitconintauto.model.CoordinateType
import com.example.bitconintauto.ui.OverlayView
import com.example.bitconintauto.util.CoordinateManager
import com.example.bitconintauto.util.OCRCaptureUtils
import com.example.bitconintauto.util.click
import com.example.bitconintauto.util.extractValue
import com.example.bitconintauto.util.isValueMatched
import com.example.bitconintauto.util.scroll

class ExecutorManager(
    private val context: Context,
    private val overlayView: OverlayView
) {
    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false
    private val intervalMs = 1000L // 루프 간격

    fun start() {
        if (isRunning) return
        isRunning = true
        Log.d("Executor", "▶▶ 루프 진입 시작됨")
        loop()
    }

    fun stop() {
        isRunning = false
        handler.removeCallbacksAndMessages(null)
        Log.d("Executor", "🛑 루프 종료됨")
    }

    private fun loop() {
        handler.postDelayed({
            if (!isRunning) return@postDelayed

            Log.d("Executor", "🌀 루프 실행 중")

            val coordinates = CoordinateManager.getAllCoordinates()
            val bitmap = OCRCaptureUtils.captureScreen(context)

            if (bitmap == null) {
                Log.d("Executor", "⚠️ 캡처 실패: bitmap == null")
                loop()
                return@postDelayed
            }

            coordinates.sortedBy { it.step }.forEach { coordinate ->
                val area = Rect(
                    coordinate.x,
                    coordinate.y,
                    coordinate.x + coordinate.width,
                    coordinate.y + coordinate.height
                )

                val value = extractValue(bitmap, area)
                val label = coordinate.label

                overlayView.drawDebugBox(area, label ?: "OCR", value)

                val match = isValueMatched(value, coordinate.expectedValue, coordinate.compareOperator)

                if (match) {
                    when (coordinate.type) {
                        CoordinateType.CLICK -> {
                            click(context, coordinate.x, coordinate.y)
                            Log.d("Executor", "🖱 클릭: ${coordinate.label}")
                        }

                        CoordinateType.SCROLL -> {
                            scroll(context, coordinate.x, coordinate.y)
                            Log.d("Executor", "📜 스크롤: ${coordinate.label}")
                        }

                        else -> {
                            // PRIMARY 또는 COPY, PASTE 등은 추후 처리
                        }
                    }
                }
            }

            loop()
        }, intervalMs)
    }
}
