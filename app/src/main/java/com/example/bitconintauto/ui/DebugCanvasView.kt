package com.example.bitconintauto.ui

import android.content.Context
import android.graphics.*
import android.view.View
import com.example.bitconintauto.model.Coordinate

class DebugCanvasView(context: Context) : View(context) {

    private val paintBox = Paint().apply {
        color = Color.RED
        strokeWidth = 4f
        style = Paint.Style.STROKE
    }

    private val paintText = Paint().apply {
        color = Color.YELLOW
        textSize = 32f
        typeface = Typeface.DEFAULT_BOLD
    }

    private var drawRect: Rect? = null
    private var debugText: String = ""

    fun drawBox(coord: Coordinate, text: String) {
        drawRect = Rect(coord.x, coord.y, coord.x + coord.width, coord.y + coord.height)
        debugText = text
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawRect?.let {
            canvas.drawRect(it, paintBox)
            canvas.drawText(debugText, it.left.toFloat(), it.top - 10f, paintText)
        }
    }
}
