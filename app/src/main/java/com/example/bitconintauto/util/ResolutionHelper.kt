package com.example.bitconintauto.util

import android.content.Context
import android.util.DisplayMetrics

object ResolutionHelper {

    private const val BASE_WIDTH = 540f
    private const val BASE_HEIGHT = 960f

    fun scaleX(context: Context, originalX: Int): Int {
        val metrics = context.resources.displayMetrics
        return (originalX * (metrics.widthPixels / BASE_WIDTH)).toInt()
    }

    fun scaleY(context: Context, originalY: Int): Int {
        val metrics = context.resources.displayMetrics
        return (originalY * (metrics.heightPixels / BASE_HEIGHT)).toInt()
    }

    fun getDeviceResolution(context: Context): Pair<Int, Int> {
        val metrics: DisplayMetrics = context.resources.displayMetrics
        return Pair(metrics.widthPixels, metrics.heightPixels)
    }
}
