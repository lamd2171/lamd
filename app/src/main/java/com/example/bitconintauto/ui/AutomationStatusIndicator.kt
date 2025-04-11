package com.example.bitconintauto.ui

import android.content.Context
import android.graphics.PixelFormat
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.example.bitconintauto.R

class AutomationStatusIndicator(private val context: Context) {
    private var view: View? = null
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    fun show() {
        if (view != null) return

        val inflater = LayoutInflater.from(context)
        view = inflater.inflate(R.layout.automation_status_indicator, null)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        )

        params.x = 10
        params.y = 100

        windowManager.addView(view, params)
    }

    fun dismiss() {
        view?.let {
            windowManager.removeView(it)
            view = null
        }
    }
}
