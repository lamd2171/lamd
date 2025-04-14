package com.example.bitconintauto.ui

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Rect
import android.view.WindowManager
import android.widget.TextView

class OCRDebugOverlay(private val context: Context) {
    private var textView: TextView? = null
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    fun show(rect: Rect, text: String) {
        dismiss()

        textView = TextView(context).apply {
            this.text = text
            setBackgroundColor(0xAA000000.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            setPadding(8, 8, 8, 8)
        }

        val params = WindowManager.LayoutParams(
            rect.width(),
            rect.height(),
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            x = rect.left
            y = rect.top
        }

        windowManager.addView(textView, params)
    }

    fun dismiss() {
        textView?.let {
            windowManager.removeView(it)
            textView = null
        }
    }
}
