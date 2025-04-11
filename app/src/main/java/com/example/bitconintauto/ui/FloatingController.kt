package com.example.bitconintauto.ui

import android.content.Context
import android.graphics.PixelFormat
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import com.example.bitconintauto.R
import com.example.bitconintauto.service.ExecutorManager

class FloatingController(private val context: Context) {
    private var floatingView: View? = null
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private val params = WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
        PixelFormat.TRANSLUCENT
    ).apply {
        x = 100
        y = 300
    }

    fun show() {
        if (floatingView != null) return

        floatingView = LayoutInflater.from(context).inflate(R.layout.floating_controller, null)
        val btnToggle = floatingView!!.findViewById<ImageButton>(R.id.btn_toggle)

        btnToggle.setOnClickListener {
            if (ExecutorManager.isRunning()) {
                ExecutorManager.stop()
                btnToggle.setImageResource(R.drawable.ic_play)
            } else {
                ExecutorManager.start(context)
                btnToggle.setImageResource(R.drawable.ic_stop)
            }
        }

        windowManager.addView(floatingView, params)
    }

    fun dismiss() {
        floatingView?.let {
            windowManager.removeView(it)
            floatingView = null
        }
    }
}
