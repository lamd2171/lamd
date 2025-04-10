package com.example.bitconintauto.util

import android.content.Context
import android.graphics.Point
import com.example.bitconintauto.model.Coordinate
import org.json.JSONArray
import java.io.File

object CoordinateManager {

    private const val FILENAME = "coordinates.json"
    private var cachedCoordinates: MutableList<Coordinate> = mutableListOf()

    fun saveCoordinates(context: Context, coordinates: List<Coordinate>) {
        val jsonArray = JSONArray()
        coordinates.forEach {
            val jsonObj = it.toJson()
            jsonArray.put(jsonObj)
        }
        File(context.filesDir, FILENAME).writeText(jsonArray.toString())
        cachedCoordinates = coordinates.toMutableList()
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
        cachedCoordinates = list
        return list
    }

    fun clearCoordinates(context: Context) {
        File(context.filesDir, FILENAME).delete()
        cachedCoordinates.clear()
    }

    fun getPrimaryCoordinate(): Coordinate? = cachedCoordinates.getOrNull(0)
    fun getCopyTarget(): Coordinate? = cachedCoordinates.getOrNull(1)
    fun getPasteTarget(): Coordinate? = cachedCoordinates.getOrNull(2)
    fun getFinalActionCoordinate(): Coordinate? = cachedCoordinates.getOrNull(3)

    fun getResolution(context: Context): Point {
        val displayMetrics = context.resources.displayMetrics
        return Point(displayMetrics.widthPixels, displayMetrics.heightPixels)
    }
}
