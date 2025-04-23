package com.example.bitconintauto.service

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import com.example.bitconintauto.model.Coordinate
import com.example.bitconintauto.model.CoordinateType
import com.example.bitconintauto.ui.OverlayView
import com.example.bitconintauto.util.ClickSimulator
import com.example.bitconintauto.util.OCRCaptureUtils
import com.example.bitconintauto.util.PreferenceHelper
import com.example.bitconintauto.util.ScreenCaptureHelper
import kotlinx.coroutines.*

class ExecutorManager {
    private var job: Job? = null

    fun start(context: Context, overlayView: OverlayView, service: AccessibilityService) {
        job = CoroutineScope(Dispatchers.Default).launch {
            delay(1000)

            val projection = ScreenCaptureHelper.getMediaProjection()
            if (projection == null) {
                Log.e("Executor", "❌ MediaProjection 객체가 null임")
                return@launch
            }

            val bitmap = ScreenCaptureHelper.captureScreen(context, projection)
            if (bitmap == null) {
                Log.e("Executor", "❌ 첫 캡처 실패")
                return@launch
            }

            val ocrText = OCRCaptureUtils.extractTextFromBitmap(bitmap)
            Log.d("Trigger", "🧠 OCR 전체 텍스트: $ocrText")

            withContext(Dispatchers.Main) {
                overlayView.updateDebugText("TriggerOCR: $ocrText")
                overlayView.drawFullScreenDebugOverlay()
            }

            val value = OCRCaptureUtils.extractNumberBeforePicn(ocrText)
            if (value >= 1.0) {
                Log.d("Trigger", "✅ PICN 조건 충족 ($value), Send 클릭 진행")

                val sendRect = OCRCaptureUtils.findWordRectFromBitmap(bitmap, "Send")
                if (sendRect != null) {
                    val screenMetrics = context.resources.displayMetrics
                    val scaledRect = ClickSimulator.scaleRect(sendRect, bitmap.width, bitmap.height, screenMetrics.widthPixels, screenMetrics.heightPixels)
                    val expandedRect = ClickSimulator.expandRect(scaledRect)

                    Log.d("Executor", "📍 클릭 좌표: $expandedRect")

                    withContext(Dispatchers.Main) {
                        overlayView.drawDebugBox(expandedRect)
                    }

                    val coord = Coordinate(
                        step = 0,
                        x = sendRect.left,
                        y = sendRect.top,
                        width = sendRect.width(),
                        height = sendRect.height(),
                        expectedValue = "Send",
                        comparator = "==",
                        type = CoordinateType.CLICK
                    )
                    PreferenceHelper.saveAllCoordinates(listOf(coord))

                    ClickSimulator.click(service, expandedRect)
                    delay(1000)
                    runSteps(context, overlayView, service, bitmap)
                } else {
                    Log.e("Executor", "❌ 'Send' 좌표 인식 실패")
                }
            } else {
                Log.d("Trigger", "⛔ PICN 조건 불충족 ($value)")
            }
        }
    }

    private suspend fun runSteps(context: Context, overlayView: OverlayView, service: AccessibilityService, bitmap: Bitmap) {
        val coordinates = PreferenceHelper.getAllCoordinates()
        Log.d("Executor", "✅ 로드된 Step 좌표 수: ${coordinates.size}")

        val screenMetrics = context.resources.displayMetrics
        val targetW = screenMetrics.widthPixels
        val targetH = screenMetrics.heightPixels

        for ((step, coord) in coordinates.withIndex()) {
            val rect = coord.toRect() ?: continue
            val scaled = ClickSimulator.scaleRect(rect, 1080, 2400, targetW, targetH)
            val expanded = ClickSimulator.expandRect(scaled)

            val result = OCRCaptureUtils.extractTextFromBitmapRegion(bitmap, scaled)
            withContext(Dispatchers.Main) {
                overlayView.updateDebugText("[$step] OCR: '$result'")
                overlayView.drawDebugBox(expanded)
            }

            Log.d("Executor", "🔁 Step $step 실행 시작")
            Log.d("Executor", "📐 Step $step OCR 대상: $scaled → 확장: $expanded")
            Log.d("Executor", "🧠 Step $step OCR 결과: '$result'")

            if (OCRCaptureUtils.isValueMatched(result, coord.expectedValue, coord.comparator)) {
                when (coord.type) {
                    CoordinateType.CLICK -> ClickSimulator.click(service, expanded)
                    CoordinateType.SCROLL -> ClickSimulator.scroll(service, expanded)
                    else -> Log.w("Executor", "⚠️ 알 수 없는 타입: ${coord.type}")
                }
            } else {
                Log.w("Executor", "⛔ Step $step 조건 불일치: OCR='$result', 기대='${coord.expectedValue}'")
            }
            delay(800)
        }
    }

    fun stop() {
        job?.cancel()
        ScreenCaptureHelper.stopProjection() // ← 여기 추가
        Log.d("Executor", "⏹️ 루프 종료됨")
    }
}
