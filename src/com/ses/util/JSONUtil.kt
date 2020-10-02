package com.ses.util

import org.json.JSONArray

class JSONArrayIterator<T>(private val arr: JSONArray) : Iterator<T> {
    var index = 0
    override fun hasNext() = index < arr.length()
    override fun next(): T = arr[index++] as T
}

fun <T> JSONArray.all(): Iterator<T> = JSONArrayIterator(this)
