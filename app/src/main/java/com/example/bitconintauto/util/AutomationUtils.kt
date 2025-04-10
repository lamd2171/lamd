package com.example.bitconintauto.util

import android.accessibilityservice.AccessibilityService
import android.graphics.Path
import com.example.bitconintauto.model.Coordinate
import com.example.bitconintauto.service.GestureBuilder
import com.example.bitconintauto.service.GesturePerformer

object AutomationUtils {

    fun getClickPathSequence(coord: Coordinate): Path {
        return GestureBuilder.buildClickPath(coord.x, coord.y)  // ✅ 수정
    }

    fun performAutoClick(service: AccessibilityService, path: Path) {
        GesturePerformer.perform(service, path)  // ✅ 저장소 기준 함수명도 perform()
    }

    // 🔧 추가: Coordinate 기반 오버로드 버전
    fun performAutoClick(service: AccessibilityService, coordinate: Coordinate) {
        val path = getClickPathSequence(coordinate)
        performAutoClick(service, path)
    }

    fun pasteValueAt(service: AccessibilityService, coord: Coordinate, value: String) {
        AutomationClipboard.setText(service, value)
        performAutoClick(service, coord)
    }

    fun performFinalActions(service: AccessibilityService, finalCoords: List<Coordinate>) {
        finalCoords.forEach { coord ->
            performAutoClick(service, coord)
        }
    }
}
