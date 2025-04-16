// app/src/main/java/com/example/bitconintauto/ui/OverlayView.kt
package com.example.bitconintauto.ui

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import com.example.bitconintauto.R
import com.example.bitconintauto.model.Coordinate
import android.view.Gravity

class OverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val view: View = LayoutInflater.from(context).inflate(R.layout.overlay_view, this, true)

    private val txtMain: TextView = view.findViewById(R.id.txt_main)
    private val txtDebug: TextView = view.findViewById(R.id.txt_debug)
    private val boxView: View = view.findViewById(R.id.debug_box)

    init {
        txtMain.setTextColor(Color.YELLOW)
        txtDebug.setTextColor(Color.GREEN)
    }

    fun updateText(text: String) {
        txtMain.text = text
    }

    fun updateDebugText(text: String) {
        txtDebug.text = text
    }

    fun drawDebugBox(coord: Coordinate, text: String = "") {
        val params = boxView.layoutParams as MarginLayoutParams
        params.width = coord.width
        params.height = coord.height
        params.leftMargin = coord.x
        params.topMargin = coord.y
        boxView.layoutParams = params
        boxView.visibility = View.VISIBLE
    }

    fun clearDebugBox() {
        boxView.visibility = View.GONE
    }

    companion object {
        private var instance: OverlayView? = null

        fun show(context: Context) {
            if (instance == null) {
                val overlay = OverlayView(context)

                val params = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,  // ✅ MATCH_PARENT 대신 WRAP_CONTENT
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or  // ✅ 포커스 안 빼앗음
                            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    android.graphics.PixelFormat.TRANSLUCENT
                )

                params.gravity = Gravity.TOP or Gravity.START
                params.x = 100  // ✅ 좌상단에 고정 위치
                params.y = 100

                val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                wm.addView(overlay, params)
                instance = overlay
            }
        }

        fun remove(context: Context) {
            instance?.let {
                val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                wm.removeView(it)
                instance = null
            }
        }

        fun getInstance(): OverlayView? {
            return instance
        }
    }
}
