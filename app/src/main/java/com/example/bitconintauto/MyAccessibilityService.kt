package com.example.bitconintauto

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.TextView
import android.widget.Toast

class MyAccessibilityService : AccessibilityService() {

    companion object {
        var instance: MyAccessibilityService? = null  // ✅ 외부에서 접근 가능하도록 설정
    }

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
        instance = this  // ✅ 자동화 루틴 호출에 필수
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
        // 자동화는 ExecutorManager가 수행
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
            WindowManager.LayoutParams.TYPE_PHONE

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

        // 👉 향후 ExecutorManager 등 실제 자동화 루틴 삽입 가능
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
