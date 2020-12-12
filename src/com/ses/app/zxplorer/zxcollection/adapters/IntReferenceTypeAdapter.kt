package com.ses.app.zxplorer.zxcollection.adapters

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.ses.app.zxplorer.zxcollection.IdList
import com.ses.app.zxplorer.zxcollection.IdText

class IntReferenceTypeAdapter<T : IdText<Int>>(val list: IdList<T>) : TypeAdapter<T>() {
    override fun write(writer: JsonWriter?, value: T) {
        writer?.value(value.id)
    }

    override fun read(reader: JsonReader?): T {
        val id = reader?.nextInt()
        return list[id]!!
    }
}