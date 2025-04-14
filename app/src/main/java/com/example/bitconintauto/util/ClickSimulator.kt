package com.example.bitconintauto.util

import android.content.Context
import android.graphics.Path
import android.graphics.Point
import android.os.SystemClock
import android.view.MotionEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.bitconintauto.model.Coordinate

class ClickSimulator(private val context: Context) {

    private fun performClick(x: Int, y: Int) {
        val downTime = SystemClock.uptimeMillis()
        val eventTime = SystemClock.uptimeMillis()
        val motionEventDown = MotionEvent.obtain(
            downTime,
            eventTime,
            MotionEvent.ACTION_DOWN,
            x.toFloat(),
            y.toFloat(),
            0
        )
        val motionEventUp = MotionEvent.obtain(
            downTime,
            eventTime + 100,
            MotionEvent.ACTION_UP,
            x.toFloat(),
            y.toFloat(),
            0
        )
        context.sendBroadcast(Intent("com.example.bitconintauto.ACTION_SIMULATE_TOUCH").apply {
            putExtra("motionEventDown", motionEventDown)
            putExtra("motionEventUp", motionEventUp)
        })
    }

    fun tap(name: String) {
        val coord = CoordinateManager.get(name).firstOrNull() ?: return
        performClick(coord.x, coord.y)
    }

    fun scrollToBottom(area: String) {
        // 단순히 아래로 3번 드래그하는 구조 (임시 구현)
        repeat(3) {
            dragDown(CoordinateManager.get(area).firstOrNull())
        }
    }

    fun scrollTo(area: String, untilName: String) {
        // 실제 조건 기반은 OCR과 연동하거나 좌표 기준
        repeat(2) {
            dragDown(CoordinateManager.get(area).firstOrNull())
        }
    }

    fun clearAndInput(name: String, text: String) {
        // 클릭 → 텍스트 삭제 → 입력 구조 필요 (간략화)
        tap(name)
        // 실제 텍스트 입력 생략
    }

    fun readValue(name: String): String {
        // OCR 또는 좌표 기반으로 값을 읽는다고 가정 (임시 더미 반환)
        return "100.000"
    }

    fun dragDown(center: Coordinate?) {
        if (center == null) return
        val startX = center.x
        val startY = center.y - 100
        val endY = center.y + 300
        // 실제 드래그 구현은 AccessibilityGesture 또는 shell 명령 필요
    }
}
