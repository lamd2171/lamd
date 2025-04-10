package com.example.bitconintauto.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

object AutomationClipboard {
    fun setText(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        clipboard?.setPrimaryClip(ClipData.newPlainText("automation", text))
    }
}
