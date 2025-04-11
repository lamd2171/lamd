package com.example.bitconintauto.ui

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.*

class TouchCaptureOverlay(
    private val context: Context,
    private val onTouchDetected: (x: Int, y: Int) -> Unit
) {

    private var overlayView: View? = null
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private val params: WindowManager.LayoutParams by lazy {
        WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, // ✅ TYPE_PHONE 제거
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        )
    }

    fun show() {
        if (overlayView != null) return

        overlayView = View(context).apply {
            setBackgroundColor(0x00000000) // 완전 투명

            setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    val x = event.rawX.toInt()
                    val y = event.rawY.toInt()

                    onTouchDetected(x, y)

                    dismiss()
                    true
                } else {
                    false
                }
            }
        }

        windowManager.addView(overlayView, params)
    }

    fun dismiss() {
        overlayView?.let {
            windowManager.removeView(it)
            overlayView = null
        }
    }
}
