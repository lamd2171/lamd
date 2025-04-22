package com.example.bitconintauto.util

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo

object ClickSimulator {

    /**
     * 🖱️ 지정된 Rect의 중심을 클릭한다 (dispatchGesture 기반)
     */
    fun click(service: AccessibilityService, rect: Rect): Boolean {
        val centerX = rect.centerX().toFloat()
        val centerY = rect.centerY().toFloat()

        Log.d("ClickSimulator", "🧭 [클릭 요청] 중심: ($centerX, $centerY)")
        Log.d("ClickSimulator", "🔱 [디바이스 해상도] ${service.resources.displayMetrics.widthPixels} x ${service.resources.displayMetrics.heightPixels}")

        val path = Path().apply { moveTo(centerX, centerY) }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 100))
            .build()

        val result = service.dispatchGesture(
            gesture,
            object : AccessibilityService.GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription?) {
                    super.onCompleted(gestureDescription)
                    Log.d("ClickSimulator", "✅ 클릭 제스처 성공: ($centerX, $centerY)")
                }

                override fun onCancelled(gestureDescription: GestureDescription?) {
                    super.onCancelled(gestureDescription)
                    Log.e("ClickSimulator", "❌ 클릭 제스처 취소됨: ($centerX, $centerY)")
                }
            },
            Handler(Looper.getMainLooper())
        )

        Log.d("ClickSimulator", "💁‍♀️ 클릭 요청 결과: $result")
        return result
    }

    /**
     * 📜 지정된 Rect를 기준으로 수직 스크롤을 수행한다
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

        service.dispatchGesture(gesture, null, null)
    }

    /**
     * 📐 해상도 보정 함수 (OCR 기준 해상도 → 실제 클릭 해상도)
     */
    fun scaleRect(rect: Rect, srcW: Int, srcH: Int, targetW: Int, targetH: Int): Rect {
        val scaleX = targetW.toFloat() / srcW
        val scaleY = targetH.toFloat() / srcH

        return Rect(
            (rect.left * scaleX).toInt(),
            ((rect.top * scaleY)-20).toInt(),
            ((rect.right * scaleX)+50).toInt(),
            ((rect.bottom * scaleY)-20).toInt()
        )
    }

    /**
     * 🟥 중심 좌표 기반 Rect를 약간 확대하여 보정 (디버깅용)
     */
    fun expandRect(rect: Rect, padding: Int = 30): Rect {
        return Rect(
            rect.left - padding,
            rect.top - padding,
            rect.right + padding,
            rect.bottom + padding
        )
    }

    /**
     * ✅ 텍스트를 기반으로 노드를 찾아 클릭 (WebView 대응 시도용)
     */
    fun clickByText(service: AccessibilityService, text: String): Boolean {
        val root = service.rootInActiveWindow ?: return false
        val nodes = root.findAccessibilityNodeInfosByText(text)

        for (node in nodes) {
            if (node.isClickable) {
                val success = node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                Log.d("WebClick", "✅ '$text' 클릭 성공 via Node: $success")
                return success
            }
        }

        Log.w("WebClick", "❌ '$text' 클릭 가능한 노드 없음")
        return false
    }
}
