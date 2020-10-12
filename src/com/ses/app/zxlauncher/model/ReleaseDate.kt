package com.ses.app.zxlauncher.model

data class ReleaseDate(val releaseYear: Int? = null, val releaseMonth: Int? = null, val releaseDay: Int? = null) : Comparable<ReleaseDate> {
    val str: String? by lazy {
        if (releaseYear != null)
            StringBuilder().apply {
                if (releaseDay != null) append(String.format("%02d/", releaseDay))
                if (releaseMonth != null) append(String.format("%02d/", releaseMonth))
                append(releaseYear)
            }.toString()
        else null
    }

    override fun compareTo(other: ReleaseDate): Int {
        var res = (releaseYear ?: 0) - (other.releaseYear ?: 0)
        if (res == 0) res = (releaseMonth ?: 0) - (other.releaseMonth ?: 0)
        if (res == 0) res = (releaseDay ?: 0) - (other.releaseDay ?: 0)
        return res
    }

    override fun toString(): String = str ?: ""
}