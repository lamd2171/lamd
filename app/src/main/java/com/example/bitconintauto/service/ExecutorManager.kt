package com.example.bitconintauto.service

import android.accessibilityservice.AccessibilityService
import android.graphics.Rect
import android.util.Log
import android.widget.TextView
import com.example.bitconintauto.ocr.OCRProcessor
import com.example.bitconintauto.ui.OCRDebugOverlay
import com.example.bitconintauto.util.PreferenceHelper
import com.example.bitconintauto.util.ScreenCaptureHelper
import kotlinx.coroutines.*

object ExecutorManager {

    private var isRunning = false
    private var job: Job? = null
    private var ocrProcessor: OCRProcessor? = null
    private var debugOverlay: OCRDebugOverlay? = null

    fun start(service: AccessibilityService, tvStatus: TextView) {
        if (isRunning) return
        isRunning = true

        ocrProcessor = OCRProcessor().apply { init(service.applicationContext) }
        debugOverlay = OCRDebugOverlay(service.applicationContext)

        job = CoroutineScope(Dispatchers.Default).launch {
            while (isRunning) {
                delay(2000)

                var text = ""
                val triggerRect = Rect(45, 240, 395, 360)  // 좌표 영역: (x=45, y=240, w=350, h=120)

                ScreenCaptureHelper.capture { fullBitmap ->
                    if (fullBitmap != null) {
                        val cropped = android.graphics.Bitmap.createBitmap(fullBitmap, triggerRect.left, triggerRect.top, triggerRect.width(), triggerRect.height())
                        text = ocrProcessor?.getText(cropped)?.trim() ?: ""
                    }
                }

                delay(300)

                val triggerValue = text.toDoubleOrNull()?.toInt() ?: 0
                val isValid = triggerValue >= 1

                withContext(Dispatchers.Main) {
                    tvStatus.text = if (isValid) "✅ 트리거 감지됨: $text" else "⏸ 감지값: $text"
                    debugOverlay?.show(triggerRect, "trigger\n$text\n${if (isValid) "OK" else "FAIL"}")
                }

                Log.d("ExecutorManager", "[Trigger OCR] 결과: $text → ${if (isValid) "작동" else "대기"}")
            }
        }
    }

    fun stop() {
        isRunning = false
        job?.cancel()
        debugOverlay?.dismiss()
        ocrProcessor?.release()
    }
}
