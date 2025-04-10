package com.example.bitconintauto.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityEvent
import android.accessibilityservice.GestureDescription
import android.graphics.Bitmap
import android.graphics.Path
import android.os.Build
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.RequiresApi
import com.example.bitconintauto.databinding.OverlayViewBinding
import com.example.bitconintauto.model.Coordinate
import com.example.bitconintauto.ocr.OCRProcessor
import com.example.bitconintauto.util.CoordinateManager
import com.example.bitconintauto.util.NumberDetector
import com.example.bitconintauto.util.OCRCaptureUtils
import com.example.bitconintauto.util.PreferenceHelper
import com.example.bitconintauto.util.ValueChangeDetector

class MyAccessibilityService : AccessibilityService() {

    private lateinit var overlayBinding: OverlayViewBinding
    private lateinit var executorManager: ExecutorManager
    private lateinit var autoClicker: AutoClicker
    private lateinit var ocrProcessor: OCRProcessor
    private lateinit var valueChangeDetector: ValueChangeDetector

    override fun onServiceConnected() {
        super.onServiceConnected()
        overlayBinding = OverlayViewBinding.inflate(layoutInflater)
        executorManager = ExecutorManager()
        autoClicker = AutoClicker(this)
        ocrProcessor = OCRProcessor().apply { init(this@MyAccessibilityService) }
        valueChangeDetector = ValueChangeDetector()

        executorManager.setInterval(3)
        executorManager.startCycle {
            performAutomation()
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {}

    @RequiresApi(Build.VERSION_CODES.N)
    private fun performAutomation() {
        val coordinates = CoordinateManager.getRegisteredCoordinates(this)
        if (coordinates.isEmpty()) return

        val primaryCoord = coordinates.first()
        val bitmap: Bitmap = OCRCaptureUtils.capture(this, primaryCoord) ?: return

        val number = NumberDetector.detectNumberAt(bitmap) ?: return
        val threshold = PreferenceHelper.getThreshold(this)

        if (valueChangeDetector.hasSignificantChange(number, threshold)) {
            val targetNode: AccessibilityNodeInfo? = findPasteTarget()
            autoClicker.executeCycle(300, 500, "${number + 0.001}", targetNode)
        }
    }

    private fun findPasteTarget(): AccessibilityNodeInfo? {
        val rootNode = rootInActiveWindow ?: return null
        return findEditableNode(rootNode)
    }

    private fun findEditableNode(node: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        if (node == null) return null
        if (node.className == "android.widget.EditText" && node.isEditable) return node

        for (i in 0 until node.childCount) {
            val result = findEditableNode(node.getChild(i))
            if (result != null) return result
        }
        return null
    }
}
