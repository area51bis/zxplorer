package com.ses.app.zxlauncher.model

interface ModelEntry {
    fun getTitle(): String
    fun getGenre(): String
    fun getReleaseYear(): Int?
    fun getReleaseDate(): ReleaseDate
    fun getMachine(): String
    fun getAvailability(): String
    fun getDownloads(): List<ModelDownload>
}
