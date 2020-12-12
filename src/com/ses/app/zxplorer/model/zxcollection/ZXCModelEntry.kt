package com.ses.app.zxplorer.model.zxcollection

import com.ses.app.zxplorer.model.Model
import com.ses.app.zxplorer.model.ModelDownload
import com.ses.app.zxplorer.model.ModelEntry
import com.ses.app.zxplorer.zxcollection.Entry
import com.ses.app.zxplorer.zxcollection.ReleaseDate

class ZXCModelEntry(model: Model, val entry: Entry) : ModelEntry(model) {
    private val _downloads: List<ZXCModelDownload> by lazy {
        ArrayList<ZXCModelDownload>(entry.downloads.size).also { list ->
            entry.downloads.forEach { list.add(ZXCModelDownload(this, it)) }
        }
    }

    val releaseYearString: String get() = entry.releaseDate?.year?.toString() ?: Model.NULL_YEAR_STRING

    override fun getTitle(): String = entry.title

    override fun getGenre(): String = entry.genre?.text ?: Model.NULL_GENRE_STRING

    override fun getReleaseYear(): Int? = entry.releaseDate?.year

    override fun getReleaseDate(): ReleaseDate = entry.releaseDate ?: ReleaseDate.EMPTY

    override fun getMachine(): String = entry.machines?.first()?.text ?: Model.NULL_MACHINE_TYPE_STRING

    override fun getAvailability(): String = entry.availability?.text ?: Model.NULL_AVAILABLE_STRING

    override fun getDownloads(): List<ModelDownload> = _downloads
}