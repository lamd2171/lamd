package com.example.bitconintauto.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi

object GesturePerformer {

    @RequiresApi(Build.VERSION_CODES.N)
    fun perform(service: AccessibilityService, path: Path, duration: Long = 100L) {
        val stroke = GestureDescription.StrokeDescription(path, 0, duration)
        val gesture = GestureDescription.Builder().addStroke(stroke).build()
        service.dispatchGesture(gesture, null, null)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun pasteText(service: AccessibilityService, x: Int, y: Int, text: String) {
        perform(service, GestureBuilder.buildClickPath(x, y))
        Handler(Looper.getMainLooper()).postDelayed({
            val clipboard = service.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            clipboard.setPrimaryClip(android.content.ClipData.newPlainText("pastedText", text))
        }, 200)
    }
}
