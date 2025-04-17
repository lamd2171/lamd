package com.example.bitconintauto.ui

import android.content.Context
import android.graphics.*
import android.view.View
import android.view.WindowManager
import com.example.bitconintauto.model.Coordinate
import com.example.bitconintauto.model.CoordinateType

/**
 * 화면 오버레이에 좌표 박스, 라벨, 디버깅 텍스트 등을 출력하는 View
 */
class OverlayView(context: Context) : View(context) {

    private val coordinates = mutableListOf<Coordinate>()
    private var debugRect: Rect? = null
    private var debugText: String = ""
    private val paintBox = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }
    private val paintText = Paint().apply {
        color = Color.YELLOW
        textSize = 36f
        style = Paint.Style.FILL
    }

    /**
     * 좌표 목록 업데이트
     */
    fun updateCoordinates(newList: List<Coordinate>) {
        coordinates.clear()
        coordinates.addAll(newList)
        invalidate()
    }

    /**
     * 디버그 박스 영역 업데이트 (OCR 인식 영역 시각화)
     */
    fun drawDebugBox(rect: Rect) {
        debugRect = rect
        invalidate()
    }

    /**
     * 디버그 텍스트 업데이트 (OCR 결과 표시)
     */
    fun updateDebugText(text: String) {
        debugText = text
        invalidate()
    }

    /**
     * 오버레이 화면에 시각적으로 그려질 요소들 정의
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 좌표 박스 및 라벨 출력
        for (coord in coordinates) {
            val box = Rect(coord.x, coord.y, coord.x + coord.width, coord.y + coord.height)
            canvas.drawRect(box, paintBox)
            canvas.drawText(
                "[${coord.label}] ${coord.expectedValue ?: ""}",
                coord.x.toFloat(),
                (coord.y - 8).toFloat(),
                paintText
            )
        }

        // 디버그 OCR 박스 출력
        debugRect?.let {
            canvas.drawRect(it, paintBox)
            canvas.drawText("OCR: $debugText", it.left.toFloat(), it.bottom + 30f, paintText)
        }
    }
}
