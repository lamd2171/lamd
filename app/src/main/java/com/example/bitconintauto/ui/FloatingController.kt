package com.example.bitconintauto.ui

import android.content.Context
import android.graphics.PixelFormat
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.Toast
import com.example.bitconintauto.service.MyAccessibilityService
import com.example.bitconintauto.R
import com.example.bitconintauto.service.ExecutorManager

class FloatingController(private val context: Context) {
    private var floatingView: View? = null
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private val layoutParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSLUCENT
    ).apply {
        x = 50
        y = 300
    }

    fun show() {
        if (floatingView != null) return

        floatingView = LayoutInflater.from(context).inflate(R.layout.floating_controller, null)
        val btnToggle = floatingView!!.findViewById<ImageButton>(R.id.btn_toggle)

        btnToggle.setImageResource(
            if (ExecutorManager.getIsRunning()) R.drawable.ic_stop else R.drawable.ic_play
        )

        btnToggle.setOnClickListener {
            val service = MyAccessibilityService.instance
            if (service == null) {
                Toast.makeText(context, "접근성 서비스가 활성화되지 않았습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            ExecutorManager.start(service) // ✅ 여기서 service는 정확히 AccessibilityService 타입으로 추론됨

            if (ExecutorManager.getIsRunning()) {
                ExecutorManager.stop()
                btnToggle.setImageResource(R.drawable.ic_play)
            } else {
                ExecutorManager.start(service)
                btnToggle.setImageResource(R.drawable.ic_stop)
            }
        }

        windowManager.addView(floatingView, layoutParams)
    }

    fun dismiss() {
        floatingView?.let {
            windowManager.removeView(it)
            floatingView = null
        }
    }
}
