package com.example.bitconintauto.util

import android.content.Context
import android.graphics.Point
import com.example.bitconintauto.model.Coordinate
import org.json.JSONArray
import java.io.File

object CoordinateManager {

    private const val FILENAME = "coordinates.json"

    fun saveCoordinates(context: Context, coordinates: List<Coordinate>) {
        val jsonArray = JSONArray()
        coordinates.forEach {
            val jsonObj = it.toJson()
            jsonArray.put(jsonObj)
        }
        File(context.filesDir, FILENAME).writeText(jsonArray.toString())
    }

    fun loadCoordinates(context: Context): List<Coordinate> {
        val file = File(context.filesDir, FILENAME)
        if (!file.exists()) return emptyList()

        val text = file.readText()
        val jsonArray = JSONArray(text)
        val list = mutableListOf<Coordinate>()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            list.add(Coordinate.fromJson(obj))
        }
        return list
    }

    fun clearCoordinates(context: Context) {
        File(context.filesDir, FILENAME).delete()
    }

    fun getResolution(context: Context): Point {
        val displayMetrics = context.resources.displayMetrics
        return Point(displayMetrics.widthPixels, displayMetrics.heightPixels)
    }
}
