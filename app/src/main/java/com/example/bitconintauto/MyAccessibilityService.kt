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
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.TextView
import android.widget.Toast

class MyAccessibilityService : AccessibilityService() {

    private lateinit var windowManager: WindowManager
    private var overlayView: TextView? = null

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
                flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
            }
        }
        serviceInfo = info
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // 이벤트 핸들링 필요 시 여기에 작성
    }

    override fun onInterrupt() {
        showOverlay("서비스 중단됨")
    }

    private fun showOverlay(text: String) {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        if (overlayView != null) return

        val inflater = LayoutInflater.from(this)
        overlayView = TextView(this).apply {
            this.text = text
            setBackgroundColor(0x80000000.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 16f
            setPadding(30, 20, 30, 20)
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        params.y = 100

        windowManager.addView(overlayView, params)
    }

    private fun performAutomation() {
        Log.d("AutoService", "자동화 작업 실행 중...")
        Toast.makeText(this, "자동화 동작 실행", Toast.LENGTH_SHORT).show()

        // 여기에 텍스트 감지 및 자동 클릭/붙여넣기/복사 등 추가
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
