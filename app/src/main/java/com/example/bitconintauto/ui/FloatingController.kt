package com.example.bitconintauto.ui

import android.content.Context
import android.graphics.PixelFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.Toast
import com.example.bitconintauto.R
import com.example.bitconintauto.service.ExecutorManager
import com.example.bitconintauto.service.MyAccessibilityService

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
                Log.e("FloatingController", "MyAccessibilityService.instance is null")
                return@setOnClickListener
            }

            if (ExecutorManager.getIsRunning()) {
                ExecutorManager.stop()
                btnToggle.setImageResource(R.drawable.ic_play)
                Toast.makeText(context, "자동화 중지됨", Toast.LENGTH_SHORT).show()
                Log.d("FloatingController", "자동화 중지 버튼 클릭됨")
            } else {
                ExecutorManager.start(service)
                btnToggle.setImageResource(R.drawable.ic_stop)
                Toast.makeText(context, "자동화 시작됨", Toast.LENGTH_SHORT).show()
                Log.d("FloatingController", "자동화 시작 버튼 클릭됨")
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
