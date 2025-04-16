package com.example.bitconintauto.ui

import android.content.Context
import android.graphics.*
import android.view.*
import android.widget.TextView
import com.example.bitconintauto.R
import com.example.bitconintauto.model.Coordinate

object OverlayView {
    private var overlayView: View? = null
    private var textView: TextView? = null
    private var debugCanvas: OverlayDebugCanvas? = null

    fun show(context: Context) {
        if (overlayView != null) return

        val inflater = LayoutInflater.from(context)
        overlayView = inflater.inflate(R.layout.overlay_view, null)
        textView = overlayView?.findViewById(R.id.txt_overlay)

        debugCanvas = OverlayDebugCanvas(context)

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

        val paramsCanvas = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        wm.addView(debugCanvas, paramsCanvas)
    }

    fun updateText(text: String) {
        textView?.text = text
    }

    fun drawDebugBox(coord: Coordinate, text: String) {
        debugCanvas?.drawBox(coord, text)
    }

    fun remove(context: Context) {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        overlayView?.let { wm.removeView(it) }
        debugCanvas?.let { wm.removeView(it) }
        overlayView = null
        debugCanvas = null
    }
}
