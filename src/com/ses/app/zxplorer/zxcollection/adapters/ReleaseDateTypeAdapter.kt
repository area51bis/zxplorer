package com.ses.app.zxplorer.zxcollection.adapters

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.ses.app.zxplorer.zxcollection.ReleaseDate

class ReleaseDateTypeAdapter : TypeAdapter<ReleaseDate>() {
    override fun write(writer: JsonWriter?, value: ReleaseDate?) {
        if (value != null) {
            writer?.value(value.toString())
        }
    }

    override fun read(reader: JsonReader?): ReleaseDate = ReleaseDate.from(reader?.nextString())
}
