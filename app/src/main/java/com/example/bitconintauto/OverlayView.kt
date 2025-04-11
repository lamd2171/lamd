package com.example.bitconintauto.overlay

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.example.bitconintauto.model.Coordinate

class OverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val rectPaint = Paint().apply {
        color = Color.RED
        strokeWidth = 3f
        style = Paint.Style.STROKE
    }

    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 28f
        isAntiAlias = true
        typeface = Typeface.DEFAULT_BOLD
    }

    private var coordinates: List<Coordinate> = emptyList()

    fun setCoordinates(coords: List<Coordinate>) {
        coordinates = coords
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (coord in coordinates) {
            val rect = Rect(
                coord.x,
                coord.y,
                coord.x + coord.width,
                coord.y + coord.height
            )
            canvas.drawRect(rect, rectPaint)

            val label = coord.label.ifBlank { "(${coord.x},${coord.y})" }
            val value = coord.expectedValue ?: ""
            val text = "$label${if (value.isNotBlank()) " [$value]" else ""}"

            canvas.drawText(text, rect.left.toFloat(), rect.top - 10f, textPaint)
        }
    }
}
