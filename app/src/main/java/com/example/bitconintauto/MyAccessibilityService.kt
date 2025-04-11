package com.example.bitconintauto

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.graphics.PixelFormat
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.TextView
import android.widget.Toast

class MyAccessibilityService : AccessibilityService() {

    private lateinit var windowManager: WindowManager
    private var overlayView: TextView? = null

    // ✅ Handler deprecated 해결
    private val handler = Handler(Looper.getMainLooper())

    private val runnable = object : Runnable {
        override fun run() {
            performAutomation()
            handler.postDelayed(this, 5000)
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        showOverlay("서비스 시작됨")
        handler.post(runnable)

        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED or AccessibilityEvent.TYPE_VIEW_CLICKED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            notificationTimeout = 100
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
                        AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
            }
        }
        serviceInfo = info
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // 필요한 경우 이벤트 처리 로직 추가
    }

    override fun onInterrupt() {
        showOverlay("서비스 중단됨")
    }

    private fun showOverlay(text: String) {
        if (!::windowManager.isInitialized) {
            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        }

        if (overlayView != null) return

        overlayView = TextView(this).apply {
            this.text = text
            setBackgroundColor(0x80000000.toInt()) // 반투명 배경
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 16f
            setPadding(30, 20, 30, 20)
        }

        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            WindowManager.LayoutParams.TYPE_PHONE // ✅ deprecated 처리

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            y = 100
        }

        windowManager.addView(overlayView, params)
    }

    private fun performAutomation() {
        Log.d("AutoService", "자동화 작업 실행 중...")
        Toast.makeText(this, "자동화 동작 실행", Toast.LENGTH_SHORT).show()

        // 👉 여기에 OCR/좌표 클릭 등의 자동화 로직 연결 가능
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
        overlayView?.let {
            windowManager.removeView(it)
            overlayView = null
        }
    }
}
