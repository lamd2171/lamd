package com.example.bitconintauto.ui

import android.content.Context
import android.graphics.*
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.example.bitconintauto.R

class OverlayView(private val context: Context) {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val overlayView: View =
        LayoutInflater.from(context).inflate(R.layout.overlay_view, null)

    private val debugBoxPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }

    private val debugTextView: TextView = overlayView.findViewById(R.id.debug_text_view)
    private var currentRect: Rect? = null

    // 오버레이 추가
    fun show() {
        if (overlayView.isAttachedToWindow) {
            android.util.Log.w("OverlayView", "⚠️ 이미 오버레이가 붙어있음, show() 생략")
            return
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.START

        windowManager.addView(overlayView, params)
    }

    // 오버레이 제거
    fun remove() {
        try {
            if (overlayView.isAttachedToWindow) {
                windowManager.removeView(overlayView)
            } else {
                // 로그로만 경고 남기고 무시
                android.util.Log.w("OverlayView", "⚠️ View not attached, skip remove()")
            }
        } catch (e: Exception) {
            android.util.Log.e("OverlayView", "❌ removeView 실패: ${e.message}")
        }
    }
    val isAttached: Boolean
        get() = overlayView.isAttachedToWindow

    // 디버그 텍스트 업데이트
    fun updateDebugText(text: String) {
        debugTextView.text = text
    }

    // 디버그 박스 그리기
    fun drawDebugBox(rect: Rect) {
        currentRect = rect
        overlayView.invalidate()
    }

    // 뷰 내에 사각형 박스를 그리기 위한 커스텀 뷰 (선택 사항)
    init {
        overlayView.setWillNotDraw(false)
        overlayView.invalidate()

        overlayView.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            overlayView.invalidate()
        }

        overlayView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        overlayView.setOnTouchListener(null)

        overlayView.viewTreeObserver.addOnDrawListener {
            val canvas = Canvas()
            currentRect?.let { rect ->
                canvas.drawRect(rect, debugBoxPaint)
            }
        }
    }
}
