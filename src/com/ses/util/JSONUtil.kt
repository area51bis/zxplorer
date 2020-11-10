package com.ses.util

import org.json.JSONArray
import org.json.JSONObject

fun JSONObject.getOrCreateJSONArray(key: String) = optJSONArray(key) ?: JSONArray().also { put(key, it) }

inline fun <reified T> JSONObject.getArray(key: String): Array<T> = optJSONArray(key)?.toArray() ?: emptyArray()

@Suppress("UNCHECKED_CAST")
class JSONArrayIterator<T>(private val arr: JSONArray) : Iterator<T> {
    var index = 0
    override fun hasNext() = index < arr.length()
    override fun next(): T = arr[index++] as T
}

fun <T> JSONArray.all(): Iterator<T> = JSONArrayIterator(this)

inline fun <reified T> JSONArray.toArray(): Array<T> = Array(length()) { this[it] as T }

fun JSONArray.clear() {
    while (!isEmpty) remove(0)
}