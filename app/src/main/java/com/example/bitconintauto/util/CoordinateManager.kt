package com.example.bitconintauto.util

import android.content.Context
import android.graphics.Rect
import com.example.bitconintauto.model.Coordinate
import com.example.bitconintauto.model.CoordinateType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * 좌표 데이터를 메모리 및 SharedPreferences에 저장/불러오기하는 매니저 클래스
 */
object CoordinateManager {
    private const val PREF_NAME = "coordinates_pref"
    private const val KEY_COORDINATES = "coordinates"

    private val coordinateList = mutableListOf<Coordinate>()

    /**
     * 현재 좌표 목록 반환
     */
    fun getAllCoordinates(): List<Coordinate> = coordinateList

    /**
     * 특정 타입의 좌표만 필터링
     */
    fun getCoordinatesByType(type: CoordinateType): List<Coordinate> {
        return coordinateList.filter { it.type == type }
    }

    /**
     * step 번호에 해당하는 클릭 좌표 반환
     */
    fun getCoordinatesForStep(step: Int): Coordinate? {
        return coordinateList.firstOrNull { it.step == step && it.type == CoordinateType.CLICK }
    }

    /**
     * 좌표 추가
     */
    fun addCoordinate(coordinate: Coordinate) {
        coordinateList.add(coordinate)
    }

    /**
     * 좌표 전체 초기화
     */
    fun resetCoordinates() {
        coordinateList.clear()
    }

    /**
     * 좌표 SharedPreferences로 저장
     */
    fun saveCoordinates(context: Context) {
        val json = Gson().toJson(coordinateList)
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_COORDINATES, json).apply()
    }

    /**
     * SharedPreferences에서 좌표 불러오기
     */
    fun loadCoordinates(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_COORDINATES, null)
        if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<List<Coordinate>>() {}.type
            val list: List<Coordinate> = Gson().fromJson(json, type)
            coordinateList.clear()
            coordinateList.addAll(list)
        }
    }
}
