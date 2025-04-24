package com.example.bitconintauto.service

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import com.example.bitconintauto.model.Coordinate
import com.example.bitconintauto.model.CoordinateType
import com.example.bitconintauto.ocr.TesseractManager
import com.example.bitconintauto.ui.OverlayView
import com.example.bitconintauto.util.ClickSimulator
import com.example.bitconintauto.util.OCRCaptureUtils
import com.example.bitconintauto.util.PreferenceHelper
import com.example.bitconintauto.util.ScreenCaptureHelper
import kotlinx.coroutines.*

object ExecutorManager {
    private var job: Job? = null

    fun start(context: Context, overlayView: OverlayView, service: AccessibilityService) {
        job = CoroutineScope(Dispatchers.Default).launch {
            delay(1000)

            val projection = ScreenCaptureHelper.getMediaProjection()
            if (projection == null) {
                Log.e("Executor", "❌ MediaProjection 객체가 null임")
                return@launch
            }

            val rawBitmap = ScreenCaptureHelper.captureScreen(context, projection, 0.1f, 1f)
            val bitmap = convertToARGB8888(rawBitmap ?: return@launch)

            val triggerOcrText = OCRCaptureUtils.extractTextFromBitmap(bitmap)
            Log.d("Trigger", "🧠 OCR 전체 텍스트: $triggerOcrText")
            Log.d("OCR_RAW", "OCR 텍스트:\n$triggerOcrText")

            withContext(Dispatchers.Main) {
                overlayView.updateDebugText("TriggerOCR: $triggerOcrText")
                overlayView.drawFullScreenDebugOverlay()
            }

            val value = OCRCaptureUtils.extractNumberBeforePicn(triggerOcrText)
            Log.d("Trigger", "✅ PICN인식값은? ($value)")
            if (value >= 1.0) {
                Log.d("Trigger", "✅ PICN 조건 충족 ($value), Send 클릭 진행")

                val sendRect = OCRCaptureUtils.findWordRectFromBitmap(bitmap, "Send", normalize = false)

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

                    val clicked = ClickSimulator.click(service, expandedRect)
                    Log.d("Executor", "✅ Trigger 클릭 결과: $clicked")

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
            val scaled = ClickSimulator.scaleRect(rect, 1080, 2337, targetW, targetH)
            val expanded = ClickSimulator.expandRect(scaled)

            val result = OCRCaptureUtils.extractTextFromBitmapRegion(bitmap, scaled)
            withContext(Dispatchers.Main) {
                overlayView.updateDebugText("[$step] OCR: '$result'")
                overlayView.drawDebugBox(expanded)
            }

            Log.d("Executor", "🔁 Step $step 실행 시작")
            Log.d("Executor", "📀 Step $step OCR 대상: $scaled → 확장: $expanded")
            Log.d("Executor", "🧠 Step $step OCR 결과: '$result'")

            if (OCRCaptureUtils.isValueMatched(result, coord.expectedValue, coord.comparator)) {
                when (coord.type) {
                    CoordinateType.CLICK -> {
                        withContext(Dispatchers.Main) {
                            overlayView.drawDebugBox(expanded) // ✅ 클릭 영역 시각화 (디버깅용)
                        }
                        val clicked = ClickSimulator.click(service, expanded)
                        Log.d("Executor", "✅ Step $step 클릭 결과: $clicked")
                    }
                    CoordinateType.SCROLL -> {
                        ClickSimulator.scroll(service, expanded)
                        Log.d("Executor", "✅ Step $step 스크롤 수행 완료")
                    }
                    else -> Log.w("Executor", "⚠️ 알 수 없는 타입: ${coord.type}")
                }
            } else {
                Log.w("Executor", "⛔ Step $step 조건 불일치: OCR='$result', 기대='${coord.expectedValue}'")
            }
            delay(800)
        }
    }

    fun stop() {
        job?.let {
            if (it.isActive) {
                it.cancel()
                Log.d("Executor", "⏹️ Coroutine 루프 종료됨")
            }
        } ?: Log.w("Executor", "⚠️ job이 null입니다.")
        ScreenCaptureHelper.stopProjection()
        Log.d("Executor", "⏹️ 미디어 프로젝션 중지됨")
    }

    private fun convertToARGB8888(src: Bitmap): Bitmap {
        return if (src.config != Bitmap.Config.ARGB_8888) {
            val converted = src.copy(Bitmap.Config.ARGB_8888, false)
            src.recycle()
            converted
        } else {
            src
        }
    }

    private fun logAllRecognizedWords(bitmap: Bitmap) {
        try {
            val tessField = TesseractManager::class.java.getDeclaredField("tessBaseAPI")
            tessField.isAccessible = true
            val tess = tessField.get(TesseractManager) as? com.googlecode.tesseract.android.TessBaseAPI ?: return

            tess.setImage(bitmap)
            tess.getUTF8Text()
            Log.d("OCR_DEBUG", "✅ setImage + recognize 완료")

            val iterator = tess.resultIterator
            if (iterator == null) {
                Log.e("OCR_DEBUG", "❌ resultIterator가 null임")
                return
            }

            val level = com.googlecode.tesseract.android.TessBaseAPI.PageIteratorLevel.RIL_WORD
            Log.d("OCR_DEBUG", "-------- 단어 인식 목록 시작 --------")
            iterator.begin()
            var count = 0
            do {
                val word = iterator.getUTF8Text(level)
                val rect = iterator.getBoundingRect(level)
                Log.d("OCR_DEBUG", "단어: '$word' → 위치: $rect")
                count++
            } while (iterator.next(level))
            Log.d("OCR_DEBUG", "-------- 단어 인식 목록 끝 (총 $count 개) --------")
        } catch (e: Exception) {
            Log.e("OCR_DEBUG", "❌ 디버그 단어 로그 출력 실패: ${e.message}")
        }
    }
}
