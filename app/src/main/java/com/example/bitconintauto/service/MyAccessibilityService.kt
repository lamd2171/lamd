package com.example.bitconintauto.service

import android.view.accessibility.AccessibilityEvent
import android.accessibilityservice.AccessibilityService
import android.os.Build
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.RequiresApi
//import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import com.example.bitconintauto.model.Coordinate
import com.example.bitconintauto.util.OCRCaptureUtils
import com.example.bitconintauto.ocr.OCRProcessor
import com.example.bitconintauto.util.CoordinateManager
import com.example.bitconintauto.util.PreferenceHelper
import com.example.bitconintauto.util.ValueChangeDetector

class MyAccessibilityService : AccessibilityService() {

    companion object {
        var instance: MyAccessibilityService? = null
    }
    private lateinit var autoClicker: AutoClicker
    private var lastDetectedValue: Double? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this // ✅ 여기에 꼭 할당
        PreferenceHelper.accessibilityService = this
        autoClicker = AutoClicker(this)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            val coord = CoordinateManager.getPrimaryCoordinate() ?: return
            val bitmap = OCRCaptureUtils.capture(this, coord) ?: return
            val text = OCRProcessor().getText(bitmap)
            val currentValue = text.toDoubleOrNull() ?: return

            val threshold = CoordinateManager.getThreshold()

            if (ValueChangeDetector.hasSignificantChange(lastDetectedValue, currentValue, threshold)) {
                lastDetectedValue = currentValue

                val clickPath = CoordinateManager.getClickPathSequence()
                val copyTarget = CoordinateManager.getCopyTarget()
                val pasteTarget = CoordinateManager.getPasteTarget()

                val offsetValue = currentValue + 0.001
                val offsetText = offsetValue.toString()

                autoClicker.executeCycle(clickPath, copyTarget, 0.001f, pasteTarget)
            }
        }
    }

    override fun onInterrupt() {
        // no-op
    }
}
