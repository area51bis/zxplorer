package com.ses.app.zxlauncher.model

abstract class ModelEntry() {
    lateinit var model: Model

    constructor(model: Model) : this() {
        this.model = model
    }

    abstract fun getTitle(): String
    abstract fun getGenre(): String
    abstract fun getReleaseYear(): Int?
    abstract fun getReleaseDate(): ReleaseDate
    abstract fun getMachine(): String
    abstract fun getAvailability(): String
    abstract fun getDownloads(): List<ModelDownload>
}
