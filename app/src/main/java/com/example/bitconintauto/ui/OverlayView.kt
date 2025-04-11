package com.example.bitconintauto.ui

import android.content.Context
import android.graphics.*
import android.view.View
import com.example.bitconintauto.model.Coordinate

class OverlayView(context: Context) : View(context) {

    private val boxPaint = Paint().apply {
        color = Color.argb(180, 255, 0, 0) // 반투명 빨강
        style = Paint.Style.STROKE
        strokeWidth = 4f
        isAntiAlias = true
    }

    private val fillPaint = Paint().apply {
        color = Color.argb(60, 255, 0, 0) // 매우 연한 빨강
        style = Paint.Style.FILL
    }

    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 36f
        typeface = Typeface.DEFAULT_BOLD
        isAntiAlias = true
    }

    private var coordinates: List<Coordinate> = emptyList()

    fun setCoordinates(coords: List<Coordinate>) {
        coordinates = coords
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        coordinates.forEachIndexed { index, coordinate ->
            val left = coordinate.x - 30f
            val top = coordinate.y - 30f
            val right = coordinate.x + 30f
            val bottom = coordinate.y + 30f

            // 배경 + 테두리
            canvas.drawRect(left, top, right, bottom, fillPaint)
            canvas.drawRect(left, top, right, bottom, boxPaint)

            // 텍스트 구성: label (또는 좌표명) + expectedValue 조건
            val label = coordinate.label.takeIf { it.isNotBlank() } ?: "C$index"
            val expected = coordinate.expectedValue?.takeIf { it.isNotBlank() } ?: ""
            val displayText = if (expected.isNotEmpty()) "$label ($expected)" else label

            // 텍스트 표시 (박스 우측 하단에)
            canvas.drawText(displayText, right + 10f, bottom, textPaint)
        }
    }
}
