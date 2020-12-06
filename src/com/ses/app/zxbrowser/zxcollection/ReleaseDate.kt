package com.ses.app.zxbrowser.zxcollection

data class ReleaseDate(val year: Int? = null, val month: Int? = null, val day: Int? = null) : Comparable<ReleaseDate> {
    companion object {
        val NULL = ReleaseDate()
    }

    val str: String? by lazy {
        if (year != null)
            StringBuilder().apply {
                if (day != null) append("%02d/".format(day))
                if (month != null) append("%02d/".format(month))
                append(year)
            }.toString()
        else null
    }

    override fun compareTo(other: ReleaseDate): Int {
        var res = (year ?: 0) - (other.year ?: 0)
        if (res == 0) res = (month ?: 0) - (other.month ?: 0)
        if (res == 0) res = (day ?: 0) - (other.day ?: 0)
        return res
    }

    override fun toString(): String = str ?: ""
}