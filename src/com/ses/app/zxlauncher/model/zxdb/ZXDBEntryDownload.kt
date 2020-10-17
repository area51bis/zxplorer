package com.ses.app.zxlauncher.model.zxdb

import com.ses.app.zxlauncher.model.EntryDownload
import com.ses.zxdb.*
import com.ses.zxdb.dao.Download

class ZXDBEntryDownload(val download: Download) : EntryDownload {
    override fun getFileName() = download.fileName
    override fun getLink() = download.file_link
    override fun getFullUrl() = download.fullUrl
    override fun getExtension() = download.extension
    override fun getFileType() = download.fileType
    override fun getFormat() = download.extension?.text
    override fun getReleaseYear() = download.release_year
    override fun getMachine() = download.machineType?.text
    override fun isImage() = download.isImage
}