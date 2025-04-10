package com.example.bitconintauto.logic

import android.content.Context
import com.example.bitconintauto.model.Coordinate
import com.example.bitconintauto.service.OcrProcessor

object CycleHandler {

    fun checkAndExecute(
        context: Context,
        coordinates: List<Coordinate>,
        threshold: Float = 1.0f,
        onDetected: () -> Unit
    ) {
        for (coord in coordinates) {
            val value = OcrProcessor.readNumberAtCoordinate(context, coord)
            if (value >= threshold) {
                onDetected()
                break
            }
        }
    }
}
