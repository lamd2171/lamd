package com.example.bitconintauto.ui

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Rect
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.example.bitconintauto.R

class OCRDebugOverlay(private val context: Context) {
    private var overlayView: View? = null
    private var windowManager: WindowManager? = null

    fun show(rect: Rect, text: String) {
        if (overlayView != null) {
            update(rect, text)
            return
        }

        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        overlayView = LayoutInflater.from(context).inflate(R.layout.overlay_ocr_debug, null)
        val tv = overlayView!!.findViewById<TextView>(R.id.tv_debug)
        tv.text = text

        val params = WindowManager.LayoutParams(
            rect.width(),
            rect.height(),
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        )
        params.x = rect.left
        params.y = rect.top
        params.gravity = Gravity.TOP or Gravity.START

        windowManager!!.addView(overlayView, params)
    }

    fun update(rect: Rect, text: String) {
        if (overlayView == null) return

        val tv = overlayView!!.findViewById<TextView>(R.id.tv_debug)
        tv.text = text

        val params = overlayView!!.layoutParams as WindowManager.LayoutParams
        params.width = rect.width()
        params.height = rect.height()
        params.x = rect.left
        params.y = rect.top
        windowManager?.updateViewLayout(overlayView, params)
    }

    fun dismiss() {
        if (overlayView != null) {
            windowManager?.removeView(overlayView)
            overlayView = null
        }
    }
}
