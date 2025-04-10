package com.example.bitconintauto.util

object ValueChangeDetector {
    private var lastValue: String? = null

    fun hasMeaningfulChange(current: String): Boolean {
        val changed = current != lastValue && current.isNotBlank()
        if (changed) lastValue = current
        return changed
    }

    fun reset() {
        lastValue = null
    }
}
