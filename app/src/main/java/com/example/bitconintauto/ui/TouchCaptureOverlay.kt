package com.example.bitconintauto.ui

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.*
import android.widget.Toast
import com.example.bitconintauto.model.Coordinate
import com.example.bitconintauto.util.CoordinateManager

class TouchCaptureOverlay(
    private val context: Context,
    private val onCoordinateCaptured: (Coordinate) -> Unit
) {

    private var overlayView: View? = null
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private val params = WindowManager.LayoutParams(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.MATCH_PARENT,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            WindowManager.LayoutParams.TYPE_PHONE,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
        PixelFormat.TRANSLUCENT
    )

    fun show() {
        if (overlayView != null) return

        overlayView = View(context).apply {
            setBackgroundColor(0x00000000) // 완전 투명

            setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    val x = event.rawX.toInt()
                    val y = event.rawY.toInt()
                    val coordinate = Coordinate(x, y)

                    // 좌표 등록
                    onCoordinateCaptured(coordinate)

                    // 사용자 피드백
                    Toast.makeText(context, "좌표 등록됨: ($x, $y)", Toast.LENGTH_SHORT).show()

                    // 오버레이 제거
                    dismiss()
                    true
                } else {
                    false
                }
            }
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
