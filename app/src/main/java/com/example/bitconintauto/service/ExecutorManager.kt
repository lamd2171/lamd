package com.example.bitconintauto.service

import android.accessibilityservice.AccessibilityService
import android.content.Context
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
import kotlin.coroutines.coroutineContext

class ExecutorManager {

    private var job: Job? = null

    /**
     * MediaProjection이 설정될 때까지 대기 후 OCR 트리거 시작
     */
    fun captureAndTriggerIfNeeded(context: Context, overlayView: OverlayView, service: AccessibilityService) {
        job = CoroutineScope(Dispatchers.Default).launch {
            delay(1000)

            var projection = ScreenCaptureHelper.getMediaProjection()
            var attempt = 0

            while (projection == null && attempt < 10) {
                Log.w("Trigger", "❗ MediaProjection 아직 null 상태. 대기 중... ($attempt)")
                delay(500)
                projection = ScreenCaptureHelper.getMediaProjection()
                attempt++
            }

            if (projection == null) {
                Log.e("Trigger", "❌ MediaProjection 설정 실패, OCR 루틴 중단")
                return@launch
            }

            triggerLoop(context, overlayView, service, projection)
        }
    }

    /**
     * 전체 OCR에서 PICN 왼쪽 숫자 판별 → 조건 만족 시 루틴 시작
     */
    private suspend fun triggerLoop(
        context: Context,
        overlayView: OverlayView,
        service: AccessibilityService,
        projection: android.media.projection.MediaProjection
    ) {
        while (coroutineContext.isActive) {
            Log.d("Trigger", "⚠️ 전체 화면 OCR 캡처 시작")

            val text = OCRCaptureUtils.extractTextFromFullScreen(context, projection)
            Log.d("Trigger", "🧠 OCR 전체 텍스트: $text")

            withContext(Dispatchers.Main) {
                overlayView.updateDebugText("TriggerOCR: $text")
                overlayView.drawFullScreenDebugOverlay()
            }

            val value = OCRCaptureUtils.extractNumberBeforePicn(text)
            if (value >= 1.0) {
                Log.d("Trigger", "✅ PICN 왼쪽 숫자 조건 충족 ($value), 루틴 시작")
                start(context, overlayView, service)
                break
            } else {
                Log.d("Trigger", "⛔ PICN 왼쪽 숫자 조건 미충족 ($value)")
            }

            delay(1000)
        }
    }

    fun start(context: Context, overlayView: OverlayView, service: AccessibilityService) {
        job = CoroutineScope(Dispatchers.Default).launch {
            Log.d("Executor", "▶▶ 루프 진입 시작됨")

            val coordinates: List<Coordinate> = PreferenceHelper.getAllCoordinates()
            if (coordinates.isEmpty()) {
                Log.e("Executor", "❌ 등록된 좌표 없음")
                return@launch
            }

            while (isActive) {
                Log.d("Executor", "🌀 루프 실행 중")

                for ((step, coord) in coordinates.withIndex()) {
                    val rect = coord.toRect() ?: continue
                    val ocrText = OCRCaptureUtils.extractTextFromRegion(context, rect)

                    withContext(Dispatchers.Main) {
                        overlayView.updateDebugText("[$step] OCR: $ocrText")
                        overlayView.drawDebugBox(rect)
                    }

                    if (OCRCaptureUtils.isValueMatched(ocrText, coord.targetText, coord.compareOperator)) {
                        when (coord.type) {
                            CoordinateType.CLICK -> {
                                Log.d("Executor", "🖱️ Step $step 클릭 실행")
                                ClickSimulator.click(service, rect)
                            }
                            CoordinateType.SCROLL -> {
                                Log.d("Executor", "📜 Step $step 스크롤 실행")
                                ClickSimulator.scroll(service, rect)
                            }
                            else -> {
                                Log.w("Executor", "⚠️ Step $step 알 수 없는 타입")
                            }
                        }
                    }

                    delay(800)
                }

                delay(1000)
            }
        }
    }

    fun stop() {
        job?.cancel()
        Log.d("Executor", "⏹️ 루프 종료됨")
    }
}
