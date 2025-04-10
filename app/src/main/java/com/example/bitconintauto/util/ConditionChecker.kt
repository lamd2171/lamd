package com.example.bitconintauto.core

object ConditionChecker {
    fun isValueValid(value: String?): Boolean {
        if (value.isNullOrEmpty()) return false
        return try {
            value.toFloat() > 1
        } catch (e: NumberFormatException) {
            false
        }
    }

    fun isValueChanged(old: String?, new: String?): Boolean {
        return old != new && !new.isNullOrEmpty()
    }
}
