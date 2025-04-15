package com.example.bitconintauto.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

class OverlayView(context: Context) : View(context) {
    private val paint: Paint = Paint()
    private var text: String = ""

    init {
        paint.color = Color.GREEN
        paint.textSize = 50f
    }

    fun setText(text: String) {
        this.text = text
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawText(text, 50f, 100f, paint)
    }
}
