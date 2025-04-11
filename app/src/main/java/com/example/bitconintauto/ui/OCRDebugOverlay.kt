package com.example.bitconintauto.ui

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import com.example.bitconintauto.R

class OCRDebugOverlay(private val context: Context) {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var container: FrameLayout? = null
    private var rectView: View? = null
    private var textView: TextView? = null
    private val handler = Handler(Looper.getMainLooper())

    fun show(x: Int, y: Int, width: Int, height: Int, text: String) {
        dismiss() // 기존 표시 제거

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        )

        container = FrameLayout(context)

        // 영역 사각형
        rectView = View(context).apply {
            background = makeDashedRectDrawable()
            layoutParams = FrameLayout.LayoutParams(width, height).apply {
                leftMargin = x
                topMargin = y
            }
        }

        // 결과 텍스트
        textView = TextView(context).apply {
            text = "OCR: $text"
            setTextColor(Color.YELLOW)
            setBackgroundColor(0x99000000.toInt())
            textSize = 14f
            setPadding(10, 4, 10, 4)
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                leftMargin = x
                topMargin = y + height + 5
            }
        }

        container?.addView(rectView)
        container?.addView(textView)
        windowManager.addView(container, params)

        // 2초 후 자동 제거
        handler.postDelayed({ dismiss() }, 2000)
    }

    fun dismiss() {
        container?.let {
            windowManager.removeView(it)
            container = null
        }
    }

    private fun makeDashedRectDrawable(): Drawable {
        val shape = ShapeDrawable(RectShape())
        shape.paint.apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 4f
            pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
        }
        return shape
    }
}
