package com.example.bitconintauto.util

import android.accessibilityservice.AccessibilityService
import android.graphics.Path
import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.GestureDescription
import kotlinx.coroutines.*

/**
 * 클릭 및 스크롤 시뮬레이션 유틸리티 클래스
 */
object ClickSimulator {
    private const val TAG = "ClickSimulator"

    /**
     * 지정한 위치를 클릭
     */
    fun click(service: AccessibilityService, x: Int, y: Int) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return

        val path = Path().apply { moveTo(x.toFloat(), y.toFloat()) }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 100))
            .build()

        val result = service.dispatchGesture(gesture, null, null)
        Log.d(TAG, "✅ 클릭 이벤트 ($x, $y): $result")
    }

    /**
     * 지정한 범위 내를 아래로 스크롤
     */
    fun scroll(service: AccessibilityService, rect: Rect) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return

        val startX = rect.centerX().toFloat()
        val startY = rect.bottom.toFloat() - 10f
        val endY = rect.top.toFloat() + 10f

        val path = Path().apply {
            moveTo(startX, startY)
            lineTo(startX, endY)
        }

        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 300))
            .build()

        val result = service.dispatchGesture(gesture, null, null)
        Log.d(TAG, "✅ 스크롤 이벤트 ($startX, $startY → $endY): $result")
    }
}
