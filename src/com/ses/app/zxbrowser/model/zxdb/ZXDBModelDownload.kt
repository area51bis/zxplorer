package com.ses.app.zxbrowser.model.zxdb

import com.ses.app.zxbrowser.model.Model
import com.ses.app.zxbrowser.model.ModelDownload
import com.ses.zxdb.*
import com.ses.zxdb.dao.Download
import com.ses.zxdb.dao.Extension
import com.ses.zxdb.dao.FileType
import java.io.File

class ZXDBModelDownload(model: Model, private val download: Download) : ModelDownload(model) {
    /**
     * Obtiene la ruta al fichero local (relativa a la biblioteca).
     */
    override fun getFilePath(): String {
        val server = download.downloadServer!!
        // file_link = "/pub/sinclair/games/a/AcroJet.tzx.zip"
        // path = "wos/games/a/AcroJet.tzx.zip"
        // quita el prefijo del servidor y añade su id.
        //return "${server.id}${File.separatorChar}${download.file_link.removePrefix(server.prefix).replace('/', File.separatorChar)}"

        // file_link = "/pub/sinclair/games/a/AcroJet.tzx.zip"
        // path = "pub/sinclair/games/a/AcroJet.tzx.zip"
        return download.file_link.removePrefix("/").replace('/', File.separatorChar)
    }
    override fun getFileName(): String = download.fileName
    override fun getLink(): String = download.file_link
    override fun getFullUrl(): String = download.fullUrl
    override fun getExtension(): Extension? = download.extension
    override fun getRawExtension(): String = download.extension?.rawExtension ?: ""
    override fun getFileType(): FileType = download.fileType
    override fun getFormat(): String? = download.extension?.text
    override fun getReleaseYear(): Int? = download.release_year
    override fun getMachine(): String? = download.machineType?.text
    override fun isImage(): Boolean = download.isImage
}