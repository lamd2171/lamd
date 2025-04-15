package com.example.bitconintauto.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

class OverlayView(context: Context) : View(context) {
    private val paint = Paint()
    private var text = ""

    init {
        paint.color = Color.GREEN
        paint.textSize = 48f
        paint.isAntiAlias = true
    }

    fun setText(text: String) {
        this.text = text
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawText(text, 50f, 100f, paint)
    }
}
