package com.example.bitconintauto.ui

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Rect
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView

class OCRDebugOverlay(private val context: Context) {
    private var textView: TextView? = null
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var currentRect: Rect? = null

    fun show(rect: Rect, text: String) {
        if (textView == null) {
            textView = TextView(context).apply {
                setBackgroundColor(0x55FF0000) // 반투명 빨강
                setTextColor(0xFFFFFFFF.toInt())
                setPadding(8, 8, 8, 8)
                textSize = 14f  // 업그레이드: 좀 더 잘 보이게
            }

            val params = WindowManager.LayoutParams(
                rect.width(),
                rect.height(),
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP or Gravity.START
                x = rect.left
                y = rect.top
            }

            windowManager.addView(textView, params)
            currentRect = rect
        }

        // 업그레이드: 상태 텍스트도 포함된 text 전체 표시
        textView?.text = text
    }

    fun dismiss() {
        textView?.let {
            windowManager.removeView(it)
            textView = null
            currentRect = null
        }
    }
}
