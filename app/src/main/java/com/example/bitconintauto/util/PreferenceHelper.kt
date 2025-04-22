package com.example.bitconintauto.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.bitconintauto.model.Coordinate
import com.example.bitconintauto.model.CoordinateType
import org.json.JSONArray
import org.json.JSONObject

object PreferenceHelper {

    private const val PREF_NAME = "bitconint_prefs"
    private const val KEY_COORDINATES = "coordinates"

    private lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    /**
     * 전체 좌표 리스트 저장
     */
    fun saveAllCoordinates(coordinateList: List<Coordinate>) {
        Log.d("PreferenceHelper", "✅ 좌표 ${coordinateList.size}개 저장됨: $coordinateList")

        val jsonArray = JSONArray()
        coordinateList.forEach { coord ->
            val json = JSONObject().apply {
                put("left", coord.x)
                put("top", coord.y)
                put("right", coord.x + coord.width)
                put("bottom", coord.y + coord.height)
                put("targetText", coord.expectedValue)
                put("compareOperator", coord.comparator)
                put("type", coord.type.name)
            }
            jsonArray.put(json)
        }
        preferences.edit().putString(KEY_COORDINATES, jsonArray.toString()).apply()
    }

    /**
     * 전체 좌표 리스트 불러오기
     */
    fun getAllCoordinates(): List<Coordinate> {
        val result = mutableListOf<Coordinate>()
        val jsonString = preferences.getString(KEY_COORDINATES, null) ?: return emptyList()
        val jsonArray = JSONArray(jsonString)

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val x = obj.getInt("left")
            val y = obj.getInt("top")
            val width = obj.getInt("right") - x
            val height = obj.getInt("bottom") - y
            val targetText = obj.optString("targetText", "")
            val compareOperator = obj.optString("compareOperator", "==")
            val type = CoordinateType.valueOf(obj.optString("type", "CLICK"))

            result.add(
                Coordinate(
                    x = x,
                    y = y,
                    width = width,
                    height = height,
                    expectedValue = targetText,
                    comparator = compareOperator,
                    type = type
                )
            )
        }

        return result
    }

    /**
     * 단일 좌표 추가 저장
     */
    fun addCoordinate(coord: Coordinate) {
        val currentList = getAllCoordinates().toMutableList()
        currentList.add(coord)
        saveAllCoordinates(currentList)
    }

    /**
     * 중복 방지 후 좌표 추가 저장
     */
    fun addCoordinateIfNotExists(newCoord: Coordinate) {
        val currentList = getAllCoordinates().toMutableList()
        val isDuplicate = currentList.any {
            it.x == newCoord.x &&
                    it.y == newCoord.y &&
                    it.width == newCoord.width &&
                    it.height == newCoord.height &&
                    it.expectedValue == newCoord.expectedValue &&
                    it.type == newCoord.type
        }

        if (!isDuplicate) {
            currentList.add(newCoord)
            saveAllCoordinates(currentList)
            Log.d("PreferenceHelper", "✅ 새 좌표 등록 완료: ${newCoord.expectedValue}")
        } else {
            Log.d("PreferenceHelper", "⚠️ 중복된 좌표 존재함: ${newCoord.expectedValue}")
        }
    }

    /**
     * 전체 좌표 삭제
     */
    fun clearCoordinates() {
        preferences.edit().remove(KEY_COORDINATES).apply()
    }
}