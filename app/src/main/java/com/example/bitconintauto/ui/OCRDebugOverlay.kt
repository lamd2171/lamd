// [6] app/src/main/java/com/example/bitconintauto/ui/OCRDebugOverlay.kt

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
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    fun show(rect: Rect, text: String) {
        dismiss()

        val inflater = LayoutInflater.from(context)
        overlayView = inflater.inflate(R.layout.debug_overlay, null)
        val tv = overlayView!!.findViewById<TextView>(R.id.debug_text)
        tv.text = text

        val params = WindowManager.LayoutParams(
            rect.width(),
            rect.height(),
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = rect.left
            y = rect.top
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