package com.example.bitconintauto.ui

import android.content.Context
import android.graphics.*
import android.view.View
import com.example.bitconintauto.model.Coordinate

class OverlayView(context: Context) : View(context) {

    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }

    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 36f
        typeface = Typeface.DEFAULT_BOLD
    }

    private var coordinates: List<Coordinate> = emptyList()

    fun setCoordinates(coords: List<Coordinate>) {
        coordinates = coords
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        coordinates.forEachIndexed { index, coordinate ->
            canvas.drawRect(
                coordinate.x - 20f,
                coordinate.y - 20f,
                coordinate.x + 20f,
                coordinate.y + 20f,
                paint
            )
            canvas.drawText("C$index", coordinate.x + 25f, coordinate.y.toFloat(), textPaint)
        }
    }
}
