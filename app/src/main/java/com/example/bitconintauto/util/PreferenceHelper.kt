package com.example.bitconintauto.util

import android.content.Context
import android.content.SharedPreferences
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
     * 좌표 리스트 저장
     */
    fun saveAllCoordinates(coordinateList: List<Coordinate>) {
        val jsonArray = JSONArray()
        coordinateList.forEach { coord ->
            val json = JSONObject().apply {
                put("left", coord.toRect().left ?: 0)
                put("top", coord.toRect().top ?: 0)
                put("right", coord.toRect().right ?: 0)
                put("bottom", coord.toRect().bottom ?: 0)
                put("targetText", coord.targetText)
                put("compareOperator", coord.compareOperator)
                put("type", coord.type.name)
            }
            jsonArray.put(json)
        }
        preferences.edit().putString(KEY_COORDINATES, jsonArray.toString()).apply()
    }

    /**
     * 저장된 좌표 리스트 불러오기
     */
    fun getAllCoordinates(): List<Coordinate> {
        val result = mutableListOf<Coordinate>()
        val jsonString = preferences.getString(KEY_COORDINATES, null) ?: return emptyList()
        val jsonArray = JSONArray(jsonString)

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val rect = android.graphics.Rect(
                obj.getInt("left"),
                obj.getInt("top"),
                obj.getInt("right"),
                obj.getInt("bottom")
            )
            val targetText = obj.optString("targetText", "")
            val compareOperator = obj.optString("compareOperator", "==")
            val type = CoordinateType.valueOf(obj.optString("type", "CLICK"))

            result.add(
                Coordinate(
                    x = obj.getInt("left"),
                    y = obj.getInt("top"),
                    width = obj.getInt("right") - obj.getInt("left"),
                    height = obj.getInt("bottom") - obj.getInt("top"),
                    expectedValue = obj.optString("targetText", ""),
                    comparator = obj.optString("compareOperator", "=="),
                    type = CoordinateType.valueOf(obj.optString("type", "CLICK"))
                )
            )
        }

        return result
    }

    /**
     * 전체 좌표 삭제
     */
    fun clearCoordinates() {
        preferences.edit().remove(KEY_COORDINATES).apply()
    }
}
