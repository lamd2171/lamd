// [9] app/src/main/java/com/example/bitconintauto/util/OverlayViewManager.kt

package com.example.bitconintauto.util

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.WindowManager

object OverlayViewManager {
    private var overlay: View? = null
    private var windowManager: WindowManager? = null

    fun showOverlay(context: Context, view: View) {
        if (overlay != null) return

        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
        }

        windowManager?.addView(view.also { overlay = it }, params)
    }

    fun removeOverlay() {
        overlay?.let {
            windowManager?.removeView(it)
            overlay = null
        }
    }
}