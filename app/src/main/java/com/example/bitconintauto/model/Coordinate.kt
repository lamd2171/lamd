package com.example.bitconintauto.model

import org.json.JSONObject

data class Coordinate(
    val x: Int,
    val y: Int,
    val width: Int = 0,
    val height: Int = 0,
    val label: String = "",
    var expectedValue: String? = null
) {
    fun toJson(): JSONObject {
        val json = JSONObject()
        json.put("x", x)
        json.put("y", y)
        json.put("width", width)
        json.put("height", height)
        json.put("label", label)
        json.put("expectedValue", expectedValue)
        return json
    }

    companion object {
        fun fromJson(json: JSONObject): Coordinate {
            return Coordinate(
                x = json.optInt("x"),
                y = json.optInt("y"),
                width = json.optInt("width", 0),
                height = json.optInt("height", 0),
                label = json.optString("label", ""),
                expectedValue = json.optString("expectedValue", null)
            )
        }
    }
}
