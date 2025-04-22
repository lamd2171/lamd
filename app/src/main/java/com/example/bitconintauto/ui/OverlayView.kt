package com.example.bitconintauto.ui

import android.content.Context
import android.graphics.*
import android.view.View
import android.view.WindowManager

class OverlayView(context: Context) : View(context) {

    private var debugText: String = ""
    private var debugBox: Rect? = null
    private var drawFullScreenOverlay: Boolean = false

    private val paintBox = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }

    private val paintText = Paint().apply {
        color = Color.YELLOW
        textSize = 32f
        isAntiAlias = true
    }

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    // ✅ TYPE_APPLICATION_OVERLAY + FLAG_NOT_TOUCHABLE + FLAG_LAYOUT_NO_LIMITS + TRANSPARENT
    private val layoutParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        PixelFormat.TRANSLUCENT
    )

    private var isAttached = false

    fun show(context: Context) {
        if (!isAttached) {
            windowManager.addView(this, layoutParams)
            isAttached = true
        }
    }

    fun remove() {
        if (isAttached) {
            windowManager.removeView(this)
            isAttached = false
        }
    }

    fun isAttached(): Boolean = isAttached

    fun updateDebugText(text: String) {
        if (debugText != text) {
            debugText = text
            invalidate()
        }
    }

    fun drawDebugBox(rect: Rect) {
        if (debugBox != rect) {
            debugBox = rect
            drawFullScreenOverlay = false
            invalidate()
        }
    }

    fun drawFullScreenDebugOverlay() {
        drawFullScreenOverlay = true
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (drawFullScreenOverlay) {
            val fullRect = Rect(0, 0, width, height)
            canvas.drawRect(fullRect, paintBox)
        }
        debugBox?.let { canvas.drawRect(it, paintBox) }
        canvas.drawText(debugText, 20f, 50f, paintText)
    }
}
