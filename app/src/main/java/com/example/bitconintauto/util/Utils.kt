package com.example.bitconintauto.util

import android.graphics.Rect
import com.example.bitconintauto.model.Coordinate

fun Coordinate.toRect(): Rect {
    return Rect(this.x, this.y, this.x + this.width, this.y + this.height)
}
