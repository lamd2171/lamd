package com.example.bitconintauto.ui

import android.content.Context
import android.graphics.PixelFormat
import android.view.*
import android.widget.TextView
import com.example.bitconintauto.R
import com.example.bitconintauto.model.Coordinate

object OverlayView {
    private var overlayView: View? = null
    private var textView: TextView? = null
    private var debugTextView: TextView? = null
    private var debugCanvasView: DebugCanvasView? = null

    fun show(context: Context) {
        if (overlayView != null) return

        val inflater = LayoutInflater.from(context)
        overlayView = inflater.inflate(R.layout.overlay_view, null)
        textView = overlayView?.findViewById(R.id.txt_overlay)
        debugTextView = overlayView?.findViewById(R.id.txt_debug)

        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val paramsText = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        paramsText.gravity = Gravity.TOP or Gravity.START
        paramsText.x = 30
        paramsText.y = 100
        wm.addView(overlayView, paramsText)

        debugCanvasView = DebugCanvasView(context)
        val paramsCanvas = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        )
        wm.addView(debugCanvasView, paramsCanvas)
    }

    fun updateText(text: String) {
        textView?.text = text
    }

    fun updateDebugText(text: String) {
        debugTextView?.text = text
    }

    fun drawDebugBox(coord: Coordinate, value: String) {
        debugCanvasView?.drawBox(coord, value)
    }

    fun remove(context: Context) {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        overlayView?.let { wm.removeView(it) }
        debugCanvasView?.let { wm.removeView(it) }
        overlayView = null
        debugCanvasView = null
    }
}
