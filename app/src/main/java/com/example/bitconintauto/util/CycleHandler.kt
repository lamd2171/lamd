package com.example.bitconintauto.automation

import com.example.bitconintauto.logic.ConditionChecker
import com.example.bitconintauto.logic.CoordinateManager
import com.example.bitconintauto.service.MyAccessibilityService
import com.example.bitconintauto.ocr.NumberDetector

class CycleHandler(
    private val service: MyAccessibilityService,
    private val coordinateManager: CoordinateManager,
    private val numberDetector: NumberDetector
) {
    private val conditionChecker = ConditionChecker()

    fun executeStep() {
        val targetCoord = coordinateManager.getPrimaryCoordinate() ?: return
        val value = numberDetector.detectNumberAt(service, targetCoord)
        if (conditionChecker.shouldTrigger(value)) {
            performAutomationSequence()
        }
    }

    private fun performAutomationSequence() {
        service.performAutoClick(coordinateManager.getClickPathSequence())
        val copiedValue = service.readValueAt(coordinateManager.getCopyTarget())
        val userOffset = coordinateManager.getUserOffset() ?: 0.001
        val finalValue = copiedValue + userOffset
        service.pasteValueAt(coordinateManager.getPasteTarget(), finalValue)
        service.performFinalActions()
    }
}
