package com.example.bitconintauto.service

import android.graphics.Path

object GestureBuilder {

    fun buildClickPath(x: Int, y: Int): Path {
        return Path().apply {
            moveTo(x.toFloat(), y.toFloat())
        }
    }
}
