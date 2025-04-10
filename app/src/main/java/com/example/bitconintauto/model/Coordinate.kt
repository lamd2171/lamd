package com.example.bitconintauto.model

import org.json.JSONObject

data class Coordinate(val x: Int, val y: Int) {

    fun toJson(): JSONObject {
        val json = JSONObject()
        json.put("x", x)
        json.put("y", y)
        return json
    }

    companion object {
        fun fromJson(json: JSONObject): Coordinate {
            val x = json.getInt("x")
            val y = json.getInt("y")
            return Coordinate(x, y)
        }
    }
}
