package com.example.bitconintauto.util

import android.graphics.Rect
import com.example.bitconintauto.model.Coordinate

object CoordinateUtils {
    fun isInsideRegion(x: Int, y: Int, region: Rect): Boolean {
        return region.contains(x, y)
    }

    fun toRect(coord: Coordinate): Rect {
        return Rect(coord.x, coord.y, coord.x + coord.width, coord.y + coord.height)
    }
}
