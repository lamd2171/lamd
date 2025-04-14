package com.example.bitconintauto.util

import android.content.Context
import android.content.SharedPreferences
import com.example.bitconintauto.model.Coordinate
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object SharedPrefUtils {
    private lateinit var preferences: SharedPreferences
    private val gson: Gson = Gson()

    fun init(context: Context) {
        preferences = context.getSharedPreferences("coordinates", Context.MODE_PRIVATE)
    }

    fun saveToPrefs(label: String, coords: List<Coordinate>) {
        val json = gson.toJson(coords)
        preferences.edit().putString(label, json).apply()
    }

    fun loadFromPrefs(label: String): List<Coordinate> {
        val json = preferences.getString(label, null) ?: return emptyList()
        val type = object : TypeToken<List<Coordinate>>() {}.type
        return gson.fromJson(json, type)
    }
}
