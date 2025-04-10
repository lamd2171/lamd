package com.example.bitconintauto.service

import com.example.bitconintauto.model.Coordinate
import com.example.bitconintauto.ocr.OcrProcessor
import com.example.bitconintauto.util.CoordinateManager
import com.example.bitconintauto.util.SoundPlayer

class ExecutorManager(
    private val coordinateManager: CoordinateManager,
    private val ocrProcessor: OcrProcessor,
    private val clickAction: (x: Int, y: Int) -> Unit,
    private val onLog: (String) -> Unit
) {
    fun executeCycle() {
        val coordinates = coordinateManager.getAllCoordinates()
        if (coordinates.isEmpty()) {
            onLog("등록된 좌표가 없습니다.")
            SoundPlayer.playError()
            return
        }

        for (coordinate in coordinates) {
            val value = ocrProcessor.readNumberAt(coordinate.x, coordinate.y)
            if (value > 1.0) {
                onLog("감지됨: (${coordinate.x}, ${coordinate.y}) = $value")
                SoundPlayer.playSuccess()
                clickAction(coordinate.x, coordinate.y)
                break
            } else {
                onLog("무시됨: (${coordinate.x}, ${coordinate.y}) = $value")
            }
        }
    }
}
