package com.ses.app.zxbrowser.zxcollection

class Entry {
    lateinit var title: String
    var genre: Genre? = null
    var machines: List<Machine>? = null
    var languages: List<Language>? = null
    var availability: List<Availability>? = null
    var releaseDate: ReleaseDate? = null

    var downloads: List<Download> = emptyList()
}
