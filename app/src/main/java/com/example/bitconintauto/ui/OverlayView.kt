package com.example.bitconintauto.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.bitconintauto.model.Coordinate

class OverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 40f
    }

    private var currentRect: Coordinate? = null
    private var displayText: String = ""

    fun showOverlayAt(coordinate: Coordinate) {
        currentRect = coordinate
        invalidate()
    }

    fun updateText(text: String) {
        displayText = text
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        currentRect?.let { coord ->
            canvas.drawRect(
                coord.x.toFloat(),
                coord.y.toFloat(),
                (coord.x + coord.width).toFloat(),
                (coord.y + coord.height).toFloat(),
                paint
            )
        }

        if (displayText.isNotBlank()) {
            canvas.drawText(displayText, 50f, 100f, textPaint)
        }
    }
}
