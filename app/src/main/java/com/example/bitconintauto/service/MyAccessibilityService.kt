// app/src/main/java/com/example/bitconintauto/service/MyAccessibilityService.kt
package com.example.bitconintauto.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class MyAccessibilityService : AccessibilityService() {


    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}

    companion object {
        var instance: MyAccessibilityService? = null
    }

}
