package com.example.bitconintauto.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View

class OverlayView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private val paintBox = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    private val paintText = Paint().apply {
        color = Color.YELLOW
        textSize = 42f
    }

    private var debugRect: Rect? = null
    private var debugText: String = ""

    fun drawDebugBox(rect: Rect) {
        debugRect = rect
        postInvalidate()
    }

    fun updateDebugText(text: String) {
        debugText = text
        postInvalidate()
    }

    fun drawFullScreenDebugOverlay() {
        debugRect = Rect(0, 0, width, height)
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        debugRect?.let { canvas.drawRect(it, paintBox) }
        if (debugText.isNotBlank()) {
            canvas.drawText(debugText, 50f, 100f, paintText)
        }
    }
}
