// app/src/main/java/com/example/bitconintauto/util/InputHelper.kt
package com.example.bitconintauto.util

import android.os.Bundle
import android.view.accessibility.AccessibilityNodeInfo

object InputHelper {
    fun inputText(root: AccessibilityNodeInfo?, text: String) {
        root?.let {
            val arguments = Bundle().apply {
                putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text)
            }
            it.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
        }
    }
}
