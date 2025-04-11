package com.example.bitconintauto.util

import android.content.Context
import com.example.bitconintauto.model.Coordinate
import org.json.JSONArray
import org.json.JSONObject

object CoordinateManager {

    private const val PREF_KEY = "saved_coordinates"
    private val registeredCoordinates = mutableMapOf<String, MutableList<Coordinate>>()
    private var onCoordinateChanged: (() -> Unit)? = null
    private var context: Context? = null  // ✅ 자동 저장용 context 보관

    // 앱 시작 시 1회 초기화
    fun init(appContext: Context) {
        context = appContext.applicationContext
    }

    fun register(name: String, coordinates: List<Coordinate>) {
        registeredCoordinates[name] = coordinates.toMutableList()
        save()
        onCoordinateChanged?.invoke()
    }

    fun append(name: String, coordinate: Coordinate) {
        val list = registeredCoordinates.getOrPut(name) { mutableListOf() }
        list.add(coordinate)
        save()
        onCoordinateChanged?.invoke()
    }

    fun reset() {
        registeredCoordinates.clear()
        save()
        onCoordinateChanged?.invoke()
    }

    fun get(name: String): List<Coordinate> {
        return registeredCoordinates[name] ?: emptyList()
    }

    fun getPrimaryCoordinate(): Coordinate? = get("primary").firstOrNull()
    fun getClickPathSequence(): List<Coordinate> = get("click")
    fun getCopyTarget(): Coordinate = get("copy").firstOrNull() ?: Coordinate(0, 0)
    fun getPasteTarget(): Coordinate = get("paste").firstOrNull() ?: Coordinate(0, 0)
    fun getFinalActions(): List<Coordinate> = get("final")
    fun getUserOffset(): Float = 0.001f
    fun getThreshold(): Double = 1.0
    fun isFirstClick(): Boolean = get("primary").isEmpty()

    fun setOnCoordinateChangedListener(listener: () -> Unit) {
        onCoordinateChanged = listener
    }

    fun removeCoordinateChangedListener() {
        onCoordinateChanged = null
    }

    // ✅ 디버그 모드 활성화 여부
    fun isDebugModeEnabled(): Boolean {
        return PreferenceHelper.getString(context ?: return false, "debug_mode") == "true"
    }

    // ✅ 내부 저장된 context로 자동 저장
    private fun save() {
        context?.let { saveToPrefs(it) }
    }

    fun saveToPrefs(context: Context) {
        val root = JSONObject()
        for ((type, coords) in registeredCoordinates) {
            val array = JSONArray()
            coords.forEach { array.put(it.toJson()) }
            root.put(type, array)
        }
        PreferenceHelper.saveString(context, PREF_KEY, root.toString())
    }

    fun loadFromPrefs(context: Context) {
        try {
            val saved = PreferenceHelper.getString(context, PREF_KEY) ?: return
            val root = JSONObject(saved)
            for (key in root.keys()) {
                val array = root.optJSONArray(key) ?: continue
                val list = mutableListOf<Coordinate>()
                for (i in 0 until array.length()) {
                    val coordJson = array.optJSONObject(i) ?: continue
                    list.add(Coordinate.fromJson(coordJson))
                }
                registeredCoordinates[key] = list
            }
            // context 저장도 같이 수행
            init(context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        onCoordinateChanged?.invoke()
    }
}
