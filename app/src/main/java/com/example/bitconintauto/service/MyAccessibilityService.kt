package com.example.bitconintauto.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Path
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import com.example.bitconintauto.databinding.OverlayViewBinding
import com.example.bitconintauto.model.Coordinate
import com.example.bitconintauto.ocr.OCRProcessor
import com.example.bitconintauto.util.PreferenceHelper
import com.example.bitconintauto.util.Utils
import com.example.bitconintauto.util.ValueChangeDetector

class MyAccessibilityService : AccessibilityService() {

    private lateinit var overlayBinding: OverlayViewBinding
    private lateinit var windowManager: WindowManager
    private var overlayAdded = false

    private lateinit var handler: Handler
    private lateinit var ocrProcessor: OCRProcessor
    private lateinit var valueChangeDetector: ValueChangeDetector
    private lateinit var prefs: PreferenceHelper

    private val coordinateList = mutableListOf<Coordinate>()
    private var running = false

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("Service", "Accessibility Service 연결됨")

        handler = Handler(Looper.getMainLooper())
        ocrProcessor = OCRProcessor(this)
        valueChangeDetector = ValueChangeDetector()
        prefs = PreferenceHelper(this)

        setupOverlay()
        loadCoordinates()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}

    private fun setupOverlay() {
        if (overlayAdded) return

        overlayBinding = OverlayViewBinding.inflate(LayoutInflater.from(this))
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        )

        overlayBinding.run {
            btnStart.setOnClickListener {
                running = !running
                if (running) {
                    btnStart.text = "중지"
                    Toast.makeText(this@MyAccessibilityService, "자동 실행 시작", Toast.LENGTH_SHORT).show()
                    handler.post(runLoop)
                } else {
                    btnStart.text = "시작"
                    handler.removeCallbacks(runLoop)
                }
            }

            btnReset.setOnClickListener {
                coordinateList.clear()
                prefs.clearCoordinates()
                Toast.makeText(this@MyAccessibilityService, "좌표 초기화됨", Toast.LENGTH_SHORT).show()
            }

            btnAddCoord.setOnClickListener {
                Utils.promptCoordinateInput(this@MyAccessibilityService) { coord ->
                    coordinateList.add(coord)
                    prefs.saveCoordinates(coordinateList)
                    Toast.makeText(this@MyAccessibilityService, "좌표 추가됨", Toast.LENGTH_SHORT).show()
                }
            }
        }

        windowManager.addView(overlayBinding.root, params)
        overlayAdded = true
    }

    private fun loadCoordinates() {
        val saved = prefs.loadCoordinates()
        coordinateList.clear()
        coordinateList.addAll(saved)
    }

    private val runLoop = object : Runnable {
        override fun run() {
            if (!running) return

            for (coord in coordinateList) {
                val bitmap = Utils.captureArea(this@MyAccessibilityService, coord)
                val text = ocrProcessor.recognizeText(bitmap)
                Log.d("OCR", "인식된 텍스트: $text")

                if (valueChangeDetector.hasMeaningfulChange(coord, text)) {
                    Log.d("Auto", "변화 감지됨, 자동화 시작")
                    executeAutomationSequence()
                    break
                }
            }

            handler.postDelayed(this, 3000)
        }
    }

    private fun executeAutomationSequence() {
        // 예시: 여러 단계의 자동화 시나리오 수행
        performClick(100, 200)
        handler.postDelayed({
            val text = copyTextAt(150, 250)
            val value = text.toDoubleOrNull()
            if (value != null) {
                val newValue = value + 0.001
                pasteTextAt(200, 300, newValue.toString())
                performClick(400, 300)
            }
        }, 1500)
    }

    private fun performClick(x: Int, y: Int) {
        val path = Path().apply {
            moveTo(x.toFloat(), y.toFloat())
        }

        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 100))
            .build()

        dispatchGesture(gesture, null, null)
    }

    private fun copyTextAt(x: Int, y: Int): String {
        performClick(x, y)
        handler.postDelayed({}, 500)

        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val item = clipboard.primaryClip?.getItemAt(0)
        return item?.text?.toString() ?: ""
    }

    private fun pasteTextAt(x: Int, y: Int, value: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("auto", value))
        handler.postDelayed({ performClick(x, y) }, 300)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (overlayAdded) {
            windowManager.removeView(overlayBinding.root)
        }
    }
}
