package com.ses.app.zxlauncher.model

interface EntryRow {
    fun getTitle(): String
    fun getGenre(): String
    fun getReleaseYear(): Int?
    fun getReleaseDate(): ReleaseDate
    fun getMachine(): String
    fun getAvailability(): String
    fun getDownloads(): List<EntryDownload>
}
