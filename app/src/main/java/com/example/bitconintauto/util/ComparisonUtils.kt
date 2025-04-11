package com.example.bitconintauto.util

object ComparisonUtils {

    fun matchCondition(value: Double, condition: String): Boolean {
        val trimmed = condition.trim()

        return when {
            trimmed.startsWith(">=") -> value >= trimmed.removePrefix(">=").toDoubleOrNull() ?: return false
            trimmed.startsWith("<=") -> value <= trimmed.removePrefix("<=").toDoubleOrNull() ?: return false
            trimmed.startsWith(">") -> value > trimmed.removePrefix(">").toDoubleOrNull() ?: return false
            trimmed.startsWith("<") -> value < trimmed.removePrefix("<").toDoubleOrNull() ?: return false
            else -> value == trimmed.toDoubleOrNull()
        }
    }
}
