package com.example.bitconintauto.util

import android.content.Context
import android.util.Log
import com.example.bitconintauto.model.Coordinate
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object PreferenceHelper {
    private const val PREF_NAME = "bit_auto_prefs"
    private const val KEY_COORDINATES = "step_coordinates"
    private lateinit var context: Context

    fun init(ctx: Context) {
        context = ctx.applicationContext
    }

    fun saveAllCoordinates(coords: List<Coordinate>) {
        try {
            val json = Gson().toJson(coords)
            val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(KEY_COORDINATES, json).apply()
            Log.d("PreferenceHelper", "✅ 좌표 ${coords.size}개 저장됨: $coords")
        } catch (e: Exception) {
            Log.e("PreferenceHelper", "❌ 좌표 저장 실패: ${e.message}")
        }
    }

    fun getAllCoordinates(): List<Coordinate> {
        return try {
            val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val json = prefs.getString(KEY_COORDINATES, null) ?: return emptyList()
            val type = object : TypeToken<List<Coordinate>>() {}.type
            Gson().fromJson(json, type)
        } catch (e: Exception) {
            Log.e("PreferenceHelper", "❌ 좌표 불러오기 실패: ${e.message}")
            emptyList()
        }
    }

    fun clearAllCoordinates() {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_COORDINATES).apply()
        Log.d("PreferenceHelper", "🧹 모든 좌표 초기화됨")
    }
}
