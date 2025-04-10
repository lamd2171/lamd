package com.example.bitconintauto.util

import android.accessibilityservice.AccessibilityService
import android.graphics.Path
import com.example.bitconintauto.service.GestureBuilder
import com.example.bitconintauto.service.GesturePerformer

object AutomationUtils {

    fun performAutoClick(service: AccessibilityService, path: Path) {
        GesturePerformer.perform(service, path, 100L)
    }

    fun getClickPathSequence(coord: Coordinate): Path {
        return GestureBuilder.buildClickPath(coord.x, coord.y)
    }

    fun pasteValueAt(service: AccessibilityService, coord: Coordinate, value: String) {
        GesturePerformer.pasteText(service, coord.x, coord.y, value)
    }

    fun performFinalActions(service: AccessibilityService, finalCoord: Coordinate?) {
        finalCoord?.let {
            val path = getClickPathSequence(it)
            performAutoClick(service, path)
        }
    }
}
