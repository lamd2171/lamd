package com.example.bitconintauto.service

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.media.projection.MediaProjection
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

    fun captureAndTriggerIfNeeded(context: Context, overlayView: OverlayView, service: AccessibilityService) {
        job = CoroutineScope(Dispatchers.Default).launch {
            delay(1000)

            val projection = ScreenCaptureHelper.getMediaProjection()
            if (projection == null) {
                Log.e("Trigger", "❌ MediaProjection 설정 실패, OCR 루틴 중단")
                return@launch
            }

            val triggerBitmap = ScreenCaptureHelper.captureScreen(context, projection)
            if (triggerBitmap == null) {
                Log.e("OCR", "❌ 전체화면 캡처 실패")
                return@launch
            }

            Log.d("Capture", "🖼️ 캡처된 이미지 해상도: ${triggerBitmap.width}x${triggerBitmap.height}")
            val text = OCRCaptureUtils.extractTextFromBitmap(triggerBitmap)
            Log.d("Trigger", "🧠 OCR 전체 텍스트: $text")

            withContext(Dispatchers.Main) {
                overlayView.updateDebugText("TriggerOCR: $text")
                overlayView.drawFullScreenDebugOverlay()
            }

            val value = OCRCaptureUtils.extractNumberBeforePicn(text)
            if (value >= 1.0) {
                Log.d("Trigger", "✅ PICN 왼쪽 숫자 조건 충족 ($value), Send 클릭 진행")

                val sendRect = OCRCaptureUtils.findWordRectFromBitmap(triggerBitmap, "Send")
                if (sendRect != null) {
                    val screenMetrics = context.resources.displayMetrics
                    val scaledRect = ClickSimulator.scaleRect(
                        sendRect, triggerBitmap.width, triggerBitmap.height,
                        screenMetrics.widthPixels, screenMetrics.heightPixels
                    )
                    val expandedRect = ClickSimulator.expandRect(scaledRect)

                    Log.d("Executor", "📍 'Send' 추정 위치 클릭: 원본=$sendRect, 스케일=$scaledRect, 확장=$expandedRect")
                    Log.d("Executor", "📱 디바이스 해상도: ${screenMetrics.widthPixels}x${screenMetrics.heightPixels}")

                    withContext(Dispatchers.Main) {
                        overlayView.drawDebugBox(expandedRect)
                    }

                    val sendCoordinate = Coordinate(
                        step = 0,
                        x = sendRect.left,
                        y = sendRect.top,
                        width = sendRect.width(),
                        height = sendRect.height(),
                        expectedValue = "Send",
                        comparator = "==",
                        type = CoordinateType.CLICK
                    )
                    PreferenceHelper.saveAllCoordinates(listOf(sendCoordinate))

                    ClickSimulator.click(service, expandedRect)
                    delay(1000)

                    // 새 MediaProjection으로 루프용 캡처 재요청
                    val loopProjection = ScreenCaptureHelper.getMediaProjection()
                    if (loopProjection == null) {
                        Log.e("Executor", "❌ 루프용 MediaProjection 획득 실패")
                        return@launch
                    }

                    val loopBitmap = ScreenCaptureHelper.captureScreen(context, loopProjection)
                    if (loopBitmap == null) {
                        Log.e("Executor", "❌ Step 루프 진입 전 캡처 실패")
                        return@launch
                    }

                    startLoopFromBitmap(context, overlayView, service, loopBitmap)
                } else {
                    Log.e("Executor", "❌ 'Send' 단어 좌표 분석 실패")
                }
            } else {
                Log.d("Trigger", "⛔ PICN 왼쪽 숫자 조건 미충족 ($value)")
            }
        }
    }

    private suspend fun startLoopFromBitmap(
        context: Context,
        overlayView: OverlayView,
        service: AccessibilityService,
        baseBitmap: Bitmap
    ) {
        val coordinates: List<Coordinate> = PreferenceHelper.getAllCoordinates()
        Log.d("Executor", "✅ 로드된 Step 좌표 개수: ${coordinates.size}")

        withContext(Dispatchers.Default) {
            Log.d("Executor", "▶▶ 루프 진입 시작됨")

            val screenMetrics = context.resources.displayMetrics
            val targetW = screenMetrics.widthPixels
            val targetH = screenMetrics.heightPixels

            for ((step, coord) in coordinates.withIndex()) {
                Log.d("Executor", "🔁 Step $step 실행 시작")
                val rect = coord.toRect() ?: continue
                val scaledRect = ClickSimulator.scaleRect(rect, 1080, 2400, targetW, targetH)
                val expandedRect = ClickSimulator.expandRect(scaledRect)

                Log.d("Executor", "📐 Step $step OCR 대상: $scaledRect → 확장: $expandedRect")
                val ocrText = OCRCaptureUtils.extractTextFromBitmapRegion(baseBitmap, scaledRect)
                Log.d("Executor", "🧠 Step $step OCR 결과: '$ocrText'")

                withContext(Dispatchers.Main) {
                    overlayView.updateDebugText("[$step] OCR: $ocrText")
                    overlayView.drawDebugBox(expandedRect)
                }

                if (OCRCaptureUtils.isValueMatched(ocrText, coord.expectedValue, coord.comparator)) {
                    when (coord.type) {
                        CoordinateType.CLICK -> {
                            Log.d("Executor", "🖱️ Step $step 클릭 실행 → $expandedRect")
                            ClickSimulator.click(service, expandedRect)
                        }
                        CoordinateType.SCROLL -> {
                            Log.d("Executor", "📜 Step $step 스크롤 실행 → $expandedRect")
                            ClickSimulator.scroll(service, expandedRect)
                        }
                        else -> {
                            Log.w("Executor", "⚠️ Step $step 알 수 없는 타입")
                        }
                    }
                } else {
                    Log.w("Executor", "⛔ Step $step 조건 불일치: OCR='$ocrText', 기대='${coord.expectedValue}', 조건='${coord.comparator}'")
                }

                delay(800)
            }
        }
    }

    fun stop() {
        job?.cancel()
        Log.d("Executor", "⏹️ 루프 종료됨")
    }
}
