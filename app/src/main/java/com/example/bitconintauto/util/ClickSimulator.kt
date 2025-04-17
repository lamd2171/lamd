package com.example.bitconintauto.util

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.util.Log

object ClickSimulator {

    // 📍 지정한 영역 중심에 클릭 이벤트 발생
    fun click(service: AccessibilityService, rect: Rect) {
        val centerX = rect.centerX().toFloat()
        val centerY = rect.centerY().toFloat()

        val path = Path().apply {
            moveTo(centerX, centerY)
        }

        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 100))
            .build()

        val dispatched = service.dispatchGesture(gesture, null, null)
        Log.d("ClickSimulator", "🖱️ 클릭 수행됨: ($centerX, $centerY), 결과: $dispatched")
    }

    // 📍 지정한 영역에 수직 스크롤 수행 (기본값 아래로 스크롤)
    fun scroll(service: AccessibilityService, rect: Rect, downward: Boolean = true) {
        val startX = rect.centerX().toFloat()
        val startY = if (downward) rect.centerY().toFloat() else rect.centerY() + 200f
        val endY = if (downward) startY + 200f else rect.centerY().toFloat()

        val path = Path().apply {
            moveTo(startX, startY)
            lineTo(startX, endY)
        }

        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 300))
            .build()

        val dispatched = service.dispatchGesture(gesture, null, Handler(Looper.getMainLooper()))
        Log.d("ClickSimulator", "🌀 스크롤 수행됨: ($startX, $startY) → ($startX, $endY), 결과: $dispatched")
    }
}
