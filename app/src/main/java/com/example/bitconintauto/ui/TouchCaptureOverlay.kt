package com.example.bitconintauto.ui

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.provider.Settings
import android.view.*
import android.widget.FrameLayout

class TouchCaptureOverlay(
    private val context: Context,
    private val onTouchDetected: (x: Int, y: Int) -> Unit
) {
    private var overlayView: View? = null
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private val layoutParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.MATCH_PARENT,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            WindowManager.LayoutParams.TYPE_PHONE,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
        PixelFormat.TRANSLUCENT
    )

    fun show() {
        if (!Settings.canDrawOverlays(context)) return
        if (overlayView != null) return

        overlayView = FrameLayout(context).apply {
            setBackgroundColor(0x00000000) // 완전 투명
            setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    onTouchDetected(event.rawX.toInt(), event.rawY.toInt())
                    dismiss()
                    true
                } else false
            }
        }

        windowManager.addView(overlayView, layoutParams)
    }

    fun dismiss() {
        overlayView?.let {
            windowManager.removeView(it)
            overlayView = null
        }
    }
}
