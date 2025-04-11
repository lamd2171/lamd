package com.example.bitconintauto.ui

import android.app.Activity
import android.content.Context
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.TextView
import com.example.bitconintauto.R
import com.example.bitconintauto.model.Coordinate
import com.example.bitconintauto.util.CoordinateManager

class CoordinateRegistrationGuide(
    private val context: Context,
    private val onComplete: () -> Unit
) {
    private val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var overlayView: View? = null
    private var currentStep = 0

    private val steps = listOf(
        "primary" to "① 숫자를 감지할 위치를 터치하세요",
        "click" to "② 클릭할 버튼의 위치를 터치하세요",
        "copy" to "③ 복사할 값을 선택하세요",
        "paste" to "④ 붙여넣을 위치를 터치하세요",
        "final" to "⑤ 마지막 완료 버튼을 눌러주세요"
    )

    private val handler = Handler(Looper.getMainLooper())

    fun start() {
        showNextStep()
    }

    private fun showNextStep() {
        if (currentStep >= steps.size) {
            removeOverlay()
            onComplete()
            return
        }

        val (type, message) = steps[currentStep]

        val inflater = LayoutInflater.from(context)
        val guideView = inflater.inflate(R.layout.overlay_registration_guide, null)
        val guideText = guideView.findViewById<TextView>(R.id.guideText)
        guideText.text = message

        guideView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val x = event.rawX.toInt()
                val y = event.rawY.toInt()
                val label = "${type}_${System.currentTimeMillis()}"
                CoordinateManager.append(type, Coordinate(x, y, label = label))
                CoordinateManager.saveToPrefs(context)
                currentStep++
                handler.postDelayed({
                    removeOverlay()
                    showNextStep()
                }, 300)
            }
            true
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.START

        try {
            wm.addView(guideView, params)
            overlayView = guideView
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun removeOverlay() {
        overlayView?.let {
            wm.removeView(it)
            overlayView = null
        }
    }
}
