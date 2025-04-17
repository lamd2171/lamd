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
import kotlinx.coroutines.*

class ExecutorManager {

    fun captureAndTriggerIfNeeded(context: Context, overlayView: OverlayView, service: AccessibilityService) {
        CoroutineScope(Dispatchers.Default).launch {
            // 🔶 트리거 사각형 고정: (x=200, y=300) ~ (x=400, y=350)
            val triggerRect = Rect(200, 300, 400, 350)

            val text = OCRCaptureUtils.extractValue(context, triggerRect)
            Log.d("Trigger", "🎯 OCR 트리거 텍스트: $text")

            withContext(Dispatchers.Main) {
                overlayView.updateDebugText("Trigger: $text")
                overlayView.drawDebugBox(triggerRect)
            }

            // 🔶 조건: 숫자 ≥ 1일 때 자동화 루틴 시작
            val value = text.toDoubleOrNull() ?: 0.0
            if (value >= 1.0) {
                Log.d("Trigger", "✅ 트리거 감지됨, 루틴 시작")
                start(context, overlayView, service)
            } else {
                Log.d("Trigger", "⛔ 트리거 조건 미충족")
            }
        }
    }


    private var job: Job? = null

    /**
     * 자동화 루틴을 시작하는 함수
     * @param context 현재 Context
     * @param overlayView 오버레이 디버깅 뷰
     * @param service 접근성 서비스 인스턴스
     */
    fun start(
        context: Context,
        overlayView: OverlayView,
        service: AccessibilityService
    ) {
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
                    val rect: Rect = coord.toRect() ?: continue

                    // OCR 대상 영역 캡처 및 텍스트 추출
                    val ocrText: String = OCRCaptureUtils.extractValue(context, rect)

                    // 디버그 오버레이 출력
                    withContext(Dispatchers.Main) {
                        overlayView.updateDebugText("[$step] OCR: $ocrText")
                        overlayView.drawDebugBox(rect)
                    }

                    // OCR 조건 일치 여부 검사
                    if (OCRCaptureUtils.isValueMatched(
                            ocrText,
                            coord.targetText,
                            coord.compareOperator
                        )
                    ) {
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

                    delay(800) // 각 스텝 간 딜레이
                }

                delay(1000) // 루프 반복 간 딜레이
            }
        }
    }

    /**
     * 자동화 루틴을 중지시키는 함수
     */
    fun stop() {
        job?.cancel()
        Log.d("Executor", "⏹️ 루프 종료됨")
    }
}
