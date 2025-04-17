package com.example.bitconintauto.util

import android.content.Context
import android.graphics.Rect
import com.example.bitconintauto.model.Coordinate
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object CoordinateManager {
    private const val PREF_NAME = "coordinate_prefs"
    private const val KEY_COORDINATES = "coordinates"
    private val gson = Gson()

    /**
     * 좌표 전체 리스트 반환
     */
    fun getAllCoordinates(context: Context): List<Pair<Int, Coordinate>> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_COORDINATES, null) ?: return emptyList()
        val type = object : TypeToken<Map<Int, Coordinate>>() {}.type
        val map: Map<Int, Coordinate> = gson.fromJson(json, type)
        return map.toList().sortedBy { it.first }
    }

    /**
     * 특정 step 번호의 좌표 반환
     */
    fun getCoordinate(context: Context, step: Int): Coordinate? {
        return getAllCoordinates(context).toMap()[step]
    }

    /**
     * step 번호별 좌표 저장
     */
    fun saveCoordinate(context: Context, step: Int, coordinate: Coordinate) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val type = object : TypeToken<Map<Int, Coordinate>>() {}.type
        val json = prefs.getString(KEY_COORDINATES, null)
        val map: MutableMap<Int, Coordinate> =
            if (json != null) gson.fromJson(json, type) else mutableMapOf()
        map[step] = coordinate
        editor.putString(KEY_COORDINATES, gson.toJson(map))
        editor.apply()
    }
}
