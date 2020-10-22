package com.ses.app.zxlauncher.model.local

import com.ses.app.zxlauncher.model.ModelEntry
import com.ses.app.zxlauncher.model.Model
import com.ses.app.zxlauncher.model.ModelDownload
import com.ses.app.zxlauncher.model.ReleaseDate
import java.io.File

class LocalModelEntry : ModelEntry {
    constructor() : super()
    constructor(model: Model) : super(model)

    lateinit var key: String
    private lateinit var _title: String
    private val _releaseDate = ReleaseDate()

    private val _downloads = ArrayList<ModelDownload>()

    companion object {
        fun getFileKey(file: File): String {
            val name = file.name
            val i = name.indexOfAny(charArrayOf('.', '('))
            return if (i != -1) name.substring(0, i) else name
        }
    }

    fun addFile(file: File) {
        if (_downloads.isEmpty()) {
            key = getFileKey(file)
            _title = extractTitle(file)
        }
        _downloads.add(LocalModelDownload(model, file))
    }

    private fun extractTitle(file: File): String {
        val key = getFileKey(file)
        return key;
    }

    override fun getTitle(): String = _title
    override fun getGenre(): String = Model.NULL_GENRE_STRING
    override fun getReleaseYear(): Int? = null
    override fun getReleaseDate(): ReleaseDate = _releaseDate
    override fun getMachine(): String = Model.NULL_MACHINE_TYPE_STRING
    override fun getAvailability(): String = ""
    override fun getDownloads(): List<ModelDownload> = _downloads
}
