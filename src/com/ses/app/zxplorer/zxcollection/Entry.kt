package com.ses.app.zxplorer.zxcollection

import com.google.gson.annotations.SerializedName

class Entry {
    lateinit var title: String
    var genre: Genre? = null
    var machines: List<Machine>? = null
    var languages: List<Language>? = null
    var availability: Availability? = null
    @SerializedName("release_date")
    var releaseDate: ReleaseDate? = null

    var downloads: List<Download> = emptyList()
}
