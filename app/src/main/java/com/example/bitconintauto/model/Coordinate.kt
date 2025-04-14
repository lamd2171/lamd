package com.example.bitconintauto.model

import org.json.JSONObject

data class Coordinate(
    val x: Int,
    val y: Int,
    val width: Int = 100,
    val height: Int = 100,
    val label: String = "",
    val expectedValue: String? = null
) {
    fun toJson(): JSONObject {
        val json = JSONObject()
        json.put("x", x)
        json.put("y", y)
        json.put("width", width)
        json.put("height", height)
        json.put("label", label)
        json.put("expectedValue", expectedValue ?: JSONObject.NULL)
        return json
    }

    companion object {
        fun fromJson(json: JSONObject): Coordinate {
            return Coordinate(
                x = json.optInt("x"),
                y = json.optInt("y"),
                width = json.optInt("width", 100),
                height = json.optInt("height", 100),
                label = json.optString("label", ""),
                expectedValue = json.optString("expectedValue", null)
            )
        }
    }

    override fun toString(): String {
        return "$x,$y"
    }
}