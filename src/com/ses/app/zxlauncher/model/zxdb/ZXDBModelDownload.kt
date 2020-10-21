package com.ses.app.zxlauncher.model.zxdb

import com.ses.app.zxlauncher.model.Model
import com.ses.app.zxlauncher.model.ModelDownload
import com.ses.zxdb.*
import com.ses.zxdb.dao.Download
import com.ses.zxdb.dao.Extension
import com.ses.zxdb.dao.FileType

class ZXDBModelDownload(model: Model, private val download: Download) : ModelDownload(model) {
    override fun getFileName(): String = download.fileName
    override fun getLink(): String = download.file_link
    override fun getFullUrl(): String = download.fullUrl
    override fun getExtension(): Extension? = download.extension
    override fun getFileType(): FileType = download.fileType
    override fun getFormat(): String? = download.extension?.text
    override fun getReleaseYear(): Int? = download.release_year
    override fun getMachine(): String? = download.machineType?.text
    override fun isImage(): Boolean = download.isImage
}