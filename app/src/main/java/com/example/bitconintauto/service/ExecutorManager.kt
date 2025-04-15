package com.example.bitconintauto.service

import android.accessibilityservice.AccessibilityService
import android.util.Log // 여기에 추가
import android.widget.Toast
import android.widget.TextView
import com.example.bitconintauto.ocr.OCRProcessor
import com.example.bitconintauto.ui.OCRDebugOverlay
import com.example.bitconintauto.util.*
import kotlinx.coroutines.*
import com.example.bitconintauto.model.Coordinate

object ExecutorManager {
    private var isRunning = false
    private var job: Job? = null
    private var ocrProcessor: OCRProcessor? = null
    private var debugOverlay: OCRDebugOverlay? = null

    fun start(service: AccessibilityService, tvStatus: TextView) {
        if (isRunning) {
            Toast.makeText(service, "이미 실행 중입니다", Toast.LENGTH_SHORT).show()
            return
        }

        isRunning = true
        tvStatus.text = "✅ 자동화 시작됨"

        CoordinateManager.set("trigger", Coordinate(45, 240, 350, 120, "trigger"))
        ocrProcessor = OCRProcessor().apply { init(service.applicationContext) }
        debugOverlay = OCRDebugOverlay(service.applicationContext)
        val click = ClickSimulator(service)

        job = CoroutineScope(Dispatchers.Default).launch {
            while (isRunning) {
                delay(2000)

                val trigger = CoordinateManager.getPrimaryCoordinate() ?: continue

                // 전체 화면 캡처 후 OCR 텍스트 추출
                ScreenCaptureHelper.capture { fullBitmap ->
                    if (fullBitmap != null) {
                        val targetBitmap = OCRCaptureUtils.captureRegion(fullBitmap, trigger)

                        // OCR 결과에서 숫자만 정제하여 추출
                        val rawText = ocrProcessor?.getText(targetBitmap)?.trim() ?: ""
                        val onlyDigits = Regex("""\d+""").find(rawText)?.value ?: ""
                        val triggerValue = onlyDigits.toIntOrNull() ?: 0

                        CoroutineScope(Dispatchers.Main).launch {
                            val status = if (triggerValue >= 1) "✅ 조건 충족" else "⏸ 조건 미달"
                            debugOverlay?.show(trigger.toRect(), "trigger\n$rawText\n$status")
                            tvStatus.text = "트리거 상태: $status"

                            if (triggerValue >= 1) {
                                Log.d("Executor", "[✅ Trigger 감지: $rawText → $triggerValue] 루틴 실행")
                                executeStepFlow(click)
                            }
                        }
                    }
                }
            }
        }
    }

    fun stop() {
        isRunning = false
        job?.cancel()
        debugOverlay?.dismiss()
        ocrProcessor?.release()
    }

    fun getIsRunning(): Boolean = isRunning

    private suspend fun executeStepFlow(click: ClickSimulator) {
        delay(500)

        val stepMap = mapOf(
            "step2" to "Send",
            "step3" to "Address Book",
            "step4" to "User",
            "step5" to "Next",
            "step6" to "Max",
            "step7" to "Next",
            "step8" to "Send",
            "step9" to "BTCT Status",
            "step10" to "My BTCT"
        )

        for ((label, keyword) in stepMap) {
            val coord = CoordinateManager.get(label).firstOrNull() ?: continue
            val result = click.readText(coord)
            Log.d("Executor", "[🔍 OCR Step] $label → \"$result\" vs \"$keyword\"")
            if (result.contains(keyword, true)) {
                click.performClick(coord)
                delay(700)
            }
        }

        click.scrollUntilVisible("step11", "scrollArea")
        delay(500)

        val copyCoord = CoordinateManager.get("step13").firstOrNull() ?: return
        click.performClick(copyCoord)
        delay(300)
        val valueText = click.readText(copyCoord)
        val value = valueText.toDoubleOrNull() ?: return
        val resultText = "%.6f".format(value + 0.001)

        val pasteCoord = CoordinateManager.get("step20").firstOrNull() ?: return
        click.clearAndInput(pasteCoord, resultText)
        delay(500)

        val balanceLabel = "step21"
        val targetLabel = "step21Check"
        var inputValue = resultText.toDoubleOrNull() ?: return

        repeat(10) {
            click.clearAndInput(pasteCoord, "%.6f".format(inputValue))
            delay(500)
            if (click.isValueMatched(balanceLabel, targetLabel)) return@repeat
            inputValue += 0.001
        }

        CoordinateManager.get("step22").firstOrNull()?.let {
            click.performClick(it)
            delay(500)
        }

        click.scrollUntilVisible("step23", "scrollArea")
        delay(500)

        val finalStepMap = mapOf(
            "step24" to "Final Confirm",
            "step25" to "Pay",
            "step26" to "Done"
        )

        for ((label, keyword) in finalStepMap) {
            val coord = CoordinateManager.get(label).firstOrNull() ?: continue
            val result = click.readText(coord)
            Log.d("Executor", "[🔍 OCR Final Step] $label → \"$result\" vs \"$keyword\"")
            if (result.contains(keyword, true)) {
                click.performClick(coord)
                delay(500)
            }
        }

        Log.d("Executor", "[🏁 루틴 종료] 트리거 감지 대기 중...")
    }
}
