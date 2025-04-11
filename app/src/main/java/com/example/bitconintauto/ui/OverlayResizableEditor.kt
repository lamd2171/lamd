package com.example.bitconintauto.ui

import android.content.Context
import android.graphics.*
import android.view.*
import android.widget.Toast
import com.example.bitconintauto.model.Coordinate
import com.example.bitconintauto.util.CoordinateManager

class OverlayResizableEditor(
    private val context: Context,
    private val coordinate: Coordinate,
    private val onComplete: () -> Unit
) : View(context) {

    private val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var layoutParams: WindowManager.LayoutParams? = null

    private val rectPaint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }

    private var startX = coordinate.x
    private var startY = coordinate.y
    private var width = coordinate.width.takeIf { it > 0 } ?: 100
    private var height = coordinate.height.takeIf { it > 0 } ?: 100

    private var resizing = false
    private val handleSize = 40

    init {
        showOverlay()
    }

    private fun showOverlay() {
        layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        wm.addView(this, layoutParams)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val left = startX.toFloat()
        val top = startY.toFloat()
        val right = (startX + width).toFloat()
        val bottom = (startY + height).toFloat()

        canvas.drawRect(left, top, right, bottom, rectPaint)

        // 드래그 핸들 그리기
        canvas.drawRect(
            right - handleSize,
            bottom - handleSize,
            right,
            bottom,
            rectPaint
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.rawX.toInt()
        val y = event.rawY.toInt()

        val inHandle = x in (startX + width - handleSize)..(startX + width) &&
                y in (startY + height - handleSize)..(startY + height)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                resizing = inHandle
            }
            MotionEvent.ACTION_MOVE -> {
                if (resizing) {
                    width = (x - startX).coerceAtLeast(50)
                    height = (y - startY).coerceAtLeast(50)
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                if (resizing) {
                    resizing = false
                    save()
                }
            }
        }
        return true
    }

    private fun save() {
        val updated = coordinate.copy(width = width, height = height)

        // 직접 좌표 교체
        CoordinateManager.replace(coordinate, updated)
        CoordinateManager.saveToPrefs(context)

        Toast.makeText(context, "영역이 저장되었습니다.", Toast.LENGTH_SHORT).show()
        wm.removeView(this)
        onComplete()
    }
}
