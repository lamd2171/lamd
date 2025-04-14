package com.example.bitconintauto.util

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import com.example.bitconintauto.model.Coordinate

object ClickSimulator {

    fun click(service: AccessibilityService, x: Int, y: Int, delay: Long = 0L) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return

        Handler(Looper.getMainLooper()).postDelayed({
            val path = Path().apply {
                moveTo(x.toFloat(), y.toFloat())
            }

            val gesture = GestureDescription.Builder()
                .addStroke(GestureDescription.StrokeDescription(path, 0, 100))
                .build()

            service.dispatchGesture(gesture, null, null)
        }, delay)
    }

    fun clickCoordinate(service: AccessibilityService, coordinate: Coordinate, delay: Long = 0L) {
        click(service, coordinate.x, coordinate.y, delay)
    }

    fun clickPathSequence(
        service: AccessibilityService,
        path: List<Coordinate>,
        delayBetween: Long = 500L
    ) {
        if (path.isEmpty()) return

        var totalDelay = 0L
        for (coord in path) {
            clickCoordinate(service, coord, totalDelay)
            totalDelay += delayBetween
        }
    }
}
