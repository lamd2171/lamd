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
        // ✅ null-safe expectedValue 처리
        json.put("expectedValue", expectedValue ?: JSONObject.NULL)
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

        // ✅ 문자열로부터 좌표 객체 파싱 (선택적 확장 기능)
        fun fromString(input: String?): Coordinate {
            if (input.isNullOrBlank()) return Coordinate(0, 0)
            val parts = input.split(",")
            return try {
                val x = parts.getOrNull(0)?.trim()?.toIntOrNull() ?: 0
                val y = parts.getOrNull(1)?.trim()?.toIntOrNull() ?: 0
                Coordinate(x, y)
            } catch (e: Exception) {
                Coordinate(0, 0)
            }
        }
    }

    override fun toString(): String {
        return "$x,$y"
    }
}
