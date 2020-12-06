package com.ses.app.zxbrowser.model.zxcollection

import com.ses.app.zxbrowser.model.Model
import com.ses.app.zxbrowser.model.ModelDownload
import com.ses.app.zxbrowser.model.ModelEntry
import com.ses.app.zxbrowser.zxcollection.Entry
import com.ses.app.zxbrowser.zxcollection.ReleaseDate

class ZXCModelEntry(model: Model, val entry: Entry) : ModelEntry(model) {
    private val _downloads: List<ZXCModelDownload> by lazy {
        ArrayList<ZXCModelDownload>(entry.downloads.size).also { list ->
            entry.downloads.forEach { list.add(ZXCModelDownload(model, it)) }
        }
    }

    override fun getTitle(): String = entry.title

    override fun getGenre(): String = entry.genre?.text ?: Model.NULL_GENRE_STRING

    override fun getReleaseYear(): Int? = entry.releaseDate?.year

    override fun getReleaseDate(): ReleaseDate = entry.releaseDate ?: ReleaseDate.NULL

    override fun getMachine(): String = entry.machines?.first()?.text ?: Model.NULL_MACHINE_TYPE_STRING

    override fun getAvailability(): String = entry.availability?.first()?.text ?: Model.NULL_AVAILABLE_STRING

    override fun getDownloads(): List<ModelDownload> = _downloads
}