package com.example.bitconintauto.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.bitconintauto.model.Coordinate

class CoordinateIndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 4f
        isAntiAlias = true
    }

    private var coordinates: List<Coordinate> = emptyList()

    fun updateCoordinates(coords: List<Coordinate>) {
        this.coordinates = coords
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return

        coordinates.forEach { coord ->
            canvas.drawCircle(coord.x.toFloat(), coord.y.toFloat(), 30f, paint)
        }
    }
}
