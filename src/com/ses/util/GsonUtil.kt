@file:Suppress("UNCHECKED_CAST")

package com.ses.util

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject


fun JsonObject.getString(memberName: String): String = getAsJsonPrimitive(memberName).asString
fun JsonObject.getJsonArray(memberName: String): JsonArray = getAsJsonArray(memberName)
fun JsonObject.getBoolean(memberName: String): Boolean = getAsJsonPrimitive(memberName).asBoolean

fun JsonObject.optString(memberName: String, defValue: String? = ""): String? = try {
    getString(memberName)
} catch (e: Exception) {
    defValue
}

fun JsonObject.optBoolean(memberName: String, defValue: Boolean = false): Boolean = try {
    getBoolean(memberName)
} catch (e: Exception) {
    defValue
}

fun JsonObject.optJsonArray(memberName: String, defValue: JsonArray? = null): JsonArray? = try {
    getJsonArray(memberName)
} catch (e: Exception) {
    defValue
}

fun JsonObject.getOrCreateJSONArray(key: String) = optJsonArray(key) ?: JsonArray().also { add(key, it) }


inline fun <reified T> JsonObject.getArray(key: String): Array<T> = optJsonArray(key)?.toArray<T>() ?: emptyArray()

fun JsonArray.clear() = with(iterator()) {
    while (hasNext()) {
        next()
        remove()
    }
}

fun JsonArray(arr: Array<*>): JsonArray = JsonArray().apply {
    arr.forEach {
        when (it) {
            is String -> add(it)
            is Number -> add(it)
            is Boolean -> add(it)
            is Char -> add(it)
            is JsonElement -> add(it)
        }
    }
}

inline fun <reified T> JsonArray.toArray(): Array<T> {
    return when (T::class) {
        String::class -> Array(size()) { this[it].asString as T }
        Number::class -> Array(size()) { this[it].asNumber as T }
        Boolean::class -> Array(size()) { this[it].asBoolean as T }
        JsonObject::class -> Array(size()) { this[it].asJsonObject as T }
        JsonArray::class -> Array(size()) { this[it].asJsonArray as T }

        else -> throw Exception("Invalid type")
    }
}
