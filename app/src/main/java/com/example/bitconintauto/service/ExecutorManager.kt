package com.example.bitconintauto.service

import android.content.Context
import android.graphics.Rect
import android.util.Log
import com.example.bitconintauto.ocr.OCRProcessor
import com.example.bitconintauto.ui.OCRDebugOverlay
import com.example.bitconintauto.util.CoordinateManager
import com.example.bitconintauto.util.OCRCaptureUtils
import kotlinx.coroutines.*

object ExecutorManager {
    private var isRunning = false
    private var job: Job? = null
    private var lastTriggerValue: Int = 0
    private var ocrProcessor: OCRProcessor? = null
    private var debugOverlay: OCRDebugOverlay? = null

    fun start(context: Context) {
        if (isRunning) return
        isRunning = true

        ocrProcessor = OCRProcessor().apply { init(context) }
        debugOverlay = OCRDebugOverlay(context)

        job = CoroutineScope(Dispatchers.Default).launch {
            while (isRunning) {
                delay(2000)
                val triggerRegion = CoordinateManager.getTriggerRegion()
                val bitmap = OCRCaptureUtils.captureRegion(triggerRegion)
                val text = bitmap?.let { ocrProcessor?.getText(it) } ?: ""

                val triggerValue = text.trim().toDoubleOrNull()?.toInt() ?: 0

                withContext(Dispatchers.Main) {
                    debugOverlay?.show(triggerRegion, text)
                }

                if (triggerValue >= 1) {
                    Log.d("ExecutorManager", "Trigger value detected: $triggerValue")
                    executeAutomationSequence(context)
                }
            }
        }
    }

    fun stop() {
        isRunning = false
        job?.cancel()
        debugOverlay?.dismiss()
        job = null
    }

    private suspend fun executeAutomationSequence(context: Context) {
        val click = ClickSimulator(context)

        // 클릭 순서 2~8 (Send > Address Book > 주소 > Next > MAX > Next > Send)
        click.tap("step2")
        delay(800)
        click.tap("step3")
        delay(800)
        click.tap("step4")
        delay(800)
        click.tap("step5")
        delay(800)
        click.tap("step6")
        delay(800)
        click.tap("step7")
        delay(800)
        click.tap("step8")
        delay(800)

        // 9~10: 상태탭 진입 → My BTCT
        click.tap("step9")
        delay(1000)
        click.tap("step10")
        delay(1000)

        // 11: 맨 아래까지 스크롤
        click.scrollToBottom("btctScrollArea")
        delay(1500)

        // 12: 가격 클릭
        click.tap("step12")
        delay(1000)

        // OCR로 Sellable 가격 가져오기
        val sellPriceBitmap = OCRCaptureUtils.captureRegion(CoordinateManager.get("step12").first())
        val sellPriceRaw = sellPriceBitmap?.let { ocrProcessor?.getText(it) } ?: "0"
        var price = (sellPriceRaw.trim().toDoubleOrNull() ?: 0.0)
        price = String.format("%.3f", price + 0.001).toDouble()

        // 14~16: X 닫기 → 뒤로 → Exchange 탭
        click.tap("step14")
        delay(500)
        click.tap("step15")
        delay(500)
        click.tap("step16")
        delay(1000)

        // 17: 스크롤
        click.scrollTo("exchangeScrollArea", "sellTabVisible")
        delay(1000)

        // 18: Order Price 입력
        click.clearAndInput("orderPriceField", price.toString())
        delay(500)

        // 조건 확인 (Sellable == Balance)
        var sellable = click.readValue("sellableValue")
        var balance = click.readValue("balanceValue")
        var attempts = 0
        while (sellable != balance && attempts < 5) {
            price += 0.001
            click.clearAndInput("orderPriceField", String.format("%.3f", price))
            delay(500)
            sellable = click.readValue("sellableValue")
            attempts++
        }

        // 19: Qty > Max 클릭
        click.tap("stepQtyMax")
        delay(500)

        // 20: Sell 버튼 클릭
        click.tap("stepSell")
        delay(1000)

        // 21: Confirm 창 → Sell 클릭
        click.tap("stepSellConfirm")
        delay(1000)

        // 22: Pay 클릭하여 메인 복귀
        click.tap("stepPay")
        delay(1000)
    }
}
