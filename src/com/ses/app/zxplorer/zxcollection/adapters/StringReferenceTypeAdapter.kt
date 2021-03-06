package com.ses.app.zxplorer.zxcollection.adapters

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.ses.app.zxplorer.zxcollection.IdList
import com.ses.app.zxplorer.zxcollection.IdText

class StringReferenceTypeAdapter<T : IdText<String>>(val list: IdList<T>) : TypeAdapter<T>() {
    override fun write(writer: JsonWriter?, value: T) {
        writer?.name("id")?.value(value.id)
    }

    override fun read(reader: JsonReader?): T {
        val id = reader?.nextString()
        return list[id]!!
    }
}