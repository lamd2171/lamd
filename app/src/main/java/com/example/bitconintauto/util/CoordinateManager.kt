package com.example.bitconintauto.util

import android.content.Context
import android.graphics.Rect
import com.example.bitconintauto.model.Coordinate
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import com.example.bitconintauto.util.CoordinateManager

object CoordinateManager {

    private val coordinates: MutableMap<String, MutableList<Coordinate>> = mutableMapOf()
    private const val fileName = "coordinates.json"

    fun append(name: String, coord: Coordinate) {
        if (!coordinates.containsKey(name)) {
            coordinates[name] = mutableListOf()
        }
        coordinates[name]?.add(coord)
    }

    fun get(name: String): List<Coordinate> {
        return coordinates[name] ?: emptyList()
    }

    fun reset() {
        coordinates.clear()
    }

    fun save(context: Context) {
        val json = JSONObject()
        coordinates.forEach { (name, list) ->
            val arr = JSONArray()
            list.forEach { arr.put(it.toJson()) }
            json.put(name, arr)
        }
        File(context.filesDir, fileName).writeText(json.toString())
    }

    fun load(context: Context) {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) return
        val json = JSONObject(file.readText())
        coordinates.clear()
        for (key in json.keys()) {
            val arr = json.getJSONArray(key)
            val list = mutableListOf<Coordinate>()
            for (i in 0 until arr.length()) {
                list.add(Coordinate.fromJson(arr.getJSONObject(i)))
            }
            coordinates[key] = list
        }
    }

    fun getTriggerRegion(): Rect {
        val trigger = get("trigger").firstOrNull() ?: return Rect(0, 0, 100, 100)
        return Rect(
            trigger.x,
            trigger.y,
            trigger.x + trigger.width,
            trigger.y + trigger.height
        )
    }
    fun getFinalActions(): List<Coordinate> {
        return registeredCoordinates["final"] ?: emptyList()
    }

    fun loadFromPrefs(context: Context) {
        val prefs = context.getSharedPreferences("coordinates", Context.MODE_PRIVATE)
        val jsonString = prefs.getString("all_coords", null) ?: return
        try {
            val jsonObject = JSONObject(jsonString)
            val restored = mutableMapOf<String, MutableList<Coordinate>>()
            jsonObject.keys().forEach { key ->
                val list = mutableListOf<Coordinate>()
                val array = jsonObject.optJSONArray(key)
                for (i in 0 until array.length()) {
                    list.add(Coordinate.fromJson(array.getJSONObject(i)))
                }
                restored[key] = list
            }
            registeredCoordinates.clear()
            registeredCoordinates.putAll(restored)
            listener?.invoke()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}