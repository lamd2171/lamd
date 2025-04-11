package com.example.bitconintauto.ui

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.example.bitconintauto.R
import android.os.Build
import android.graphics.PixelFormat

class TutorialOverlay(private val context: Context) {

    private var overlayView: View? = null
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var currentStep = 0

    private val steps = listOf(
        "① 화면을 터치해서 자동화할 좌표를 등록하세요.",
        "② 등록된 좌표는 오버레이로 표시되며 자동 인식됩니다.",
        "③ 오른쪽 버튼으로 자동 실행을 시작/중지할 수 있습니다."
    )

    fun show() {
        if (overlayView != null) return

        val inflater = LayoutInflater.from(context)
        overlayView = inflater.inflate(R.layout.tutorial_overlay, null)

        val textView = overlayView!!.findViewById<TextView>(R.id.tutorial_text)
        val nextButton = overlayView!!.findViewById<Button>(R.id.btn_next)

        textView.text = steps[currentStep]

        nextButton.setOnClickListener {
            currentStep++
            if (currentStep < steps.size) {
                textView.text = steps[currentStep]
            } else {
                dismiss()
            }
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )

        overlayView!!.setBackgroundColor(Color.parseColor("#66000000"))
        windowManager.addView(overlayView, params)
    }

    fun dismiss() {
        overlayView?.let {
            windowManager.removeView(it)
            overlayView = null
        }
    }
}
