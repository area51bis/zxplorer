package com.ses.util

import org.json.JSONArray

@Suppress("UNCHECKED_CAST")
class JSONArrayIterator<T>(private val arr: JSONArray, transform: ((Int) -> T)? = null) : Iterator<T> {
    var index = 0
    override fun hasNext() = index < arr.length()
    override fun next(): T = arr[index++] as T
}

fun <T> JSONArray.all(transform: ((Int) -> T)? = null): Iterator<T> = JSONArrayIterator(this, transform)

fun JSONArray.clear() {
    while (!isEmpty) remove(0)
}