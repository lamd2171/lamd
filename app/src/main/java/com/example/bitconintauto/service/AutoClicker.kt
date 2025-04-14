package com.example.bitconintauto.service

import android.accessibilityservice.AccessibilityService
import com.example.bitconintauto.model.Coordinate
import com.example.bitconintauto.util.ClickSimulator
import kotlinx.coroutines.*

class AutoClicker(private val service: AccessibilityService) {

    private val scope = CoroutineScope(Dispatchers.Default)

    fun executeCycle(
        clickPath: List<Coordinate>,
        copyTarget: Coordinate?,
        offset: Float,
        pasteTarget: Coordinate?
    ) {
        scope.launch {
            val click = ClickSimulator(service)

            clickPath.forEach {
                click.performClick(it)
                delay(300)
            }

            val copiedValue = copyTarget?.let { click.readText(it) }?.toFloatOrNull() ?: return@launch
            val finalValue = copiedValue + offset
            pasteTarget?.let {
                click.clearAndInput(it, "%.3f".format(finalValue))
                delay(300)
            }

            click.performClick("stepSell")
            delay(500)
            click.performClick("stepSellConfirm")
            delay(500)
        }
    }
}
