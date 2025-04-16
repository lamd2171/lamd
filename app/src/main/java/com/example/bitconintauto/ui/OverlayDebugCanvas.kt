package com.example.bitconintauto.ui

import android.content.Context
import android.graphics.*
import android.view.View
import com.example.bitconintauto.model.Coordinate

class OverlayDebugCanvas(context: Context) : View(context) {

    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
        color = Color.RED
        isAntiAlias = true
    }

    private val textPaint = Paint().apply {
        color = Color.YELLOW
        textSize = 40f
        isAntiAlias = true
    }

    private var currentBox: Coordinate? = null
    private var currentText: String = ""

    fun drawBox(coord: Coordinate, text: String) {
        currentBox = coord
        currentText = text
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        currentBox?.let { box ->
            val rect = Rect(box.x, box.y, box.x + box.width, box.y + box.height)
            canvas.drawRect(rect, paint)
            canvas.drawText(currentText, box.x.toFloat(), (box.y - 10).toFloat(), textPaint)
        }
    }
}
