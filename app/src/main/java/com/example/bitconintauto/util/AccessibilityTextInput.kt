package com.example.bitconintauto.util

import android.accessibilityservice.AccessibilityService
import android.os.Bundle
import android.view.accessibility.AccessibilityNodeInfo

object AccessibilityTextInput {
    fun sendText(service: AccessibilityService, text: String) {
        val rootNode = service.rootInActiveWindow ?: return
        val inputNode = findEditable(rootNode) ?: return
        val args = Bundle()
        args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text)
        inputNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)
    }

    private fun findEditable(node: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        if (node == null) return null
        if (node.isEditable) return node
        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            val result = findEditable(child)
            if (result != null) return result
        }
        return null
    }
}
