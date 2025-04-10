package com.example.bitconintauto.overlay

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class OverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val rectPaint = Paint().apply {
        color = Color.RED
        strokeWidth = 3f
        style = Paint.Style.STROKE
    }

    private var overlayRect: Rect? = null
    private var startX = 0
    private var startY = 0

    var onRegionSelected: ((Rect) -> Unit)? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        overlayRect?.let { canvas.drawRect(it, rectPaint) }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x.toInt()
                startY = event.y.toInt()
                overlayRect = Rect(startX, startY, startX, startY)
                invalidate()
            }

            MotionEvent.ACTION_MOVE -> {
                overlayRect?.right = event.x.toInt()
                overlayRect?.bottom = event.y.toInt()
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                overlayRect?.let {
                    it.sort()
                    onRegionSelected?.invoke(it)
                }
            }
        }
        return true
    }

    fun clearOverlay() {
        overlayRect = null
        invalidate()
    }

    fun getOverlayRect(): Rect? = overlayRect
}

private fun Rect.sort() {
    val left = minOf(this.left, this.right)
    val top = minOf(this.top, this.bottom)
    val right = maxOf(this.left, this.right)
    val bottom = maxOf(this.top, this.bottom)
    this.set(left, top, right, bottom)
}
