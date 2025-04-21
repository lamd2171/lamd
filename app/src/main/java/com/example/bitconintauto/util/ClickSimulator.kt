package com.example.bitconintauto.util

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.util.Log

object ClickSimulator {

    /**
     * 🖱️ 지정된 Rect의 중심을 클릭한다.
     * @param service 접근성 서비스 인스턴스
     * @param rect 클릭할 좌표 영역
     */
    fun click(service: AccessibilityService, rect: Rect) {
        val centerX = rect.centerX().toFloat()
        val centerY = rect.centerY().toFloat()

        val path = Path().apply {
            moveTo(centerX, centerY)
        }

        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 100))
            .build()

        val dispatched = service.dispatchGesture(
            gesture,
            object : AccessibilityService.GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription?) {
                    Log.d("ClickSimulator", "✅ 클릭 완료")
                }

                override fun onCancelled(gestureDescription: GestureDescription?) {
                    Log.w("ClickSimulator", "⚠️ 클릭 취소됨")
                }
            },
            Handler(Looper.getMainLooper())
        )

        Log.d("ClickSimulator", "🖱️ 클릭 수행됨: ($centerX, $centerY), 결과: $dispatched")
    }

    /**
     * 📜 지정된 Rect를 기준으로 수직 스크롤을 수행한다.
     * @param service 접근성 서비스
     * @param rect 기준 좌표
     * @param downward true면 아래로, false면 위로 스크롤
     */
    fun scroll(service: AccessibilityService, rect: Rect, downward: Boolean = true) {
        val startX = rect.centerX().toFloat()
        val distance = 300f
        val startY = rect.centerY().toFloat()
        val endY = if (downward) startY + distance else startY - distance

        val path = Path().apply {
            moveTo(startX, startY)
            lineTo(startX, endY)
        }

        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 300))
            .build()

        val dispatched = service.dispatchGesture(
            gesture,
            object : AccessibilityService.GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription?) {
                    Log.d("ClickSimulator", "✅ 스크롤 완료")
                }

                override fun onCancelled(gestureDescription: GestureDescription?) {
                    Log.w("ClickSimulator", "⚠️ 스크롤 취소됨")
                }
            },
            Handler(Looper.getMainLooper())
        )

        Log.d("ClickSimulator", "🌀 스크롤 수행됨: ($startX, $startY) → ($startX, $endY), 결과: $dispatched")
    }
}
