package com.example.bitconintauto.ui

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.provider.Settings
import android.view.*
import android.widget.FrameLayout

class TouchCaptureOverlay(private val context: Context) {

    private var windowManager: WindowManager? = null
    private var overlayView: View? = null

    fun show(onTouchDetected: (x: Int, y: Int) -> Unit) {
        // 오버레이 권한이 없으면 실행하지 않음
        if (!Settings.canDrawOverlays(context)) {
            return
        }

        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        )

        val container = FrameLayout(context).apply {
            setBackgroundColor(0x33000000) // 약간 반투명 회색 배경
            isClickable = true
            isFocusable = true
        }

        // 터치 이벤트 리스너
        container.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val x = event.rawX.toInt()
                val y = event.rawY.toInt()
                onTouchDetected(x, y) // 콜백 호출
                dismiss()
                true
            } else false
        }

        overlayView = container
        windowManager?.addView(container, layoutParams)
    }

    fun dismiss() {
        overlayView?.let {
            windowManager?.removeView(it)
            overlayView = null
        }
    }
}
