package com.example.bitconintauto.ui

import android.app.Service
import android.content.Context
import android.graphics.PixelFormat
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.example.bitconintauto.R

object OverlayView {
    private var overlayView: View? = null
    private var txtOverlay: TextView? = null
    private var txtDebug: TextView? = null

    fun show(context: Context) {
        if (overlayView != null) return

        val inflater = context.getSystemService(Service.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        overlayView = inflater.inflate(R.layout.overlay_view, null)

        txtOverlay = overlayView?.findViewById(R.id.txt_overlay)
        txtDebug = overlayView?.findViewById(R.id.txt_debug)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        )

        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.addView(overlayView, params)
    }

    fun updateText(text: String) {
        txtOverlay?.text = text
    }

    fun updateDebugText(text: String) {
        txtDebug?.visibility = View.VISIBLE
        txtDebug?.text = "DEBUG → $text"
    }

    fun remove(context: Context) {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if (overlayView != null) {
            wm.removeView(overlayView)
            overlayView = null
        }
    }
}
