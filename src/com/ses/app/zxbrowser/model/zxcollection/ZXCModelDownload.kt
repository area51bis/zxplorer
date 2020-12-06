package com.ses.app.zxbrowser.model.zxcollection

import com.ses.app.zxbrowser.model.Model
import com.ses.app.zxbrowser.model.ModelDownload
import com.ses.app.zxbrowser.model.ModelFileExtension
import com.ses.app.zxbrowser.zxcollection.Download
import com.ses.zxdb.dao.Extension
import com.ses.zxdb.dao.FileType

class ZXCModelDownload(model: Model, private val download: Download) : ModelDownload(model) {
    private val _fileType: FileType by lazy {
        FileType().apply {
            id = download.fileType?.id!!
            text = download.fileType?.text!!
        }
    }

    private val _rawExtension: String by lazy {
        download.extension
                ?: getFileName()
                        .toLowerCase()
                        .removeSuffix(".zip")
                        .substringAfterLast('.')
    }

    override fun getType(): Type = Type.File

    override fun getFilePath(): String {
        return download.fileLink.removePrefix("http://").removePrefix("https://")
    }

    override fun getFileName(): String = download.fileName
            ?: download.fileLink.substringAfterLast('/').substringBeforeLast('?')

    override fun getLink(): String = download.fileLink

    override fun getFullUrl(): String = download.fileLink

    override fun getExtension(): Extension? = null

    override fun getRawExtension(): String = _rawExtension

    override fun getFileType(): FileType = _fileType

    override fun getFormat(): String? = null

    override fun getReleaseYear(): Int? = download.releaseDate?.year

    override fun getMachine(): String? = download.machine?.text

    override fun isImage(): Boolean = ModelFileExtension.isImage(getRawExtension())

    override fun getSource(): String = ""
}