package com.example.bitconintauto.ui

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import com.example.bitconintauto.R

class FirstClickGuideOverlay(private val context: Context) {
    private var overlayView: View? = null
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    fun show(onDismiss: () -> Unit) {
        if (overlayView != null) return

        val inflater = LayoutInflater.from(context)
        overlayView = inflater.inflate(R.layout.first_click_guide, null)

        val dismissButton = overlayView!!.findViewById<Button>(R.id.btn_close_guide)
        dismissButton.setOnClickListener {
            dismiss()
            onDismiss()
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            android.graphics.PixelFormat.TRANSLUCENT
        )

        overlayView!!.setBackgroundColor(Color.parseColor("#77000000"))
        windowManager.addView(overlayView, params)
    }

    private fun dismiss() {
        overlayView?.let {
            windowManager.removeView(it)
            overlayView = null
        }
    }
}
