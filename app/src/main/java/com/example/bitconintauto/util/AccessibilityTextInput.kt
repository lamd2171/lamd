// [11] app/src/main/java/com/example/bitconintauto/util/AccessibilityTextInput.kt

package com.example.bitconintauto.util

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

object AccessibilityTextInput {
    fun sendText(service: AccessibilityService, text: String) {
        val root = service.rootInActiveWindow ?: return
        val inputNode = findFocusedEditableNode(root) ?: return
        inputNode.performAction(AccessibilityNodeInfo.ACTION_FOCUS)
        val args = android.os.Bundle().apply {
            putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text)
        }
        inputNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)
    }

    private fun findFocusedEditableNode(node: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        if (node == null) return null
        if (node.isFocused && node.isEditable) return node
        for (i in 0 until node.childCount) {
            val result = findFocusedEditableNode(node.getChild(i))
            if (result != null) return result
        }
        return null
    }
}
