package com.ses.app.zxplorer.zxcollection.adapters

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.ses.app.zxplorer.zxcollection.ReleaseDate

class ReleaseDateTypeAdapter : TypeAdapter<ReleaseDate>() {
    override fun write(writer: JsonWriter?, value: ReleaseDate?) {
        if (value != null) {
            StringBuilder().apply {
                if (value.year != null) {
                    append(value.year)
                    if (value.month != null) {
                        append('-').append(value.month)
                        if (value.day != null) {
                            append('-').append(value.day)
                        }
                    }
                }
            }
        }

    }

    override fun read(reader: JsonReader?): ReleaseDate {
        val a = reader?.nextString()?.split('-', '/')
        return if (a != null) {
            val year = if (a.size > 0) a[0].toInt() else null
            val month = if (a.size > 1) a[1].toInt() else null
            val day = if (a.size > 2) a[2].toInt() else null
            ReleaseDate(year, month, day)
        } else {
            ReleaseDate()
        }
    }
}
