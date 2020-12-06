package com.ses.app.zxbrowser.model.local

import com.ses.app.zxbrowser.model.*
import com.ses.app.zxbrowser.zxcollection.ReleaseDate

class LocalModelEntry : ModelEntry {
    constructor() : super()
    constructor(model: Model) : super(model)

    lateinit var key: String
    private lateinit var _title: String
    private val _releaseDate = ReleaseDate()
    private lateinit var _machine: String

    private val _downloads = ArrayList<ModelDownload>()

    fun addFile(download: LocalModelDownload) {
        if (_downloads.isEmpty()) {
            val nameParser = download.nameExtractor
            key = nameParser.baseName
            _title = nameParser.title
            _machine = if (nameParser.is128) "ZX-Spectrum 128K" else "ZX-Spectrum 48K"
        }
        _downloads.add(download)
    }

    override fun getTitle(): String = _title
    override fun getGenre(): String = ""
    override fun getReleaseYear(): Int? = null
    override fun getReleaseDate(): ReleaseDate = _releaseDate
    override fun getMachine(): String = _machine
    override fun getAvailability(): String = ""
    override fun getDownloads(): List<ModelDownload> = _downloads
}
