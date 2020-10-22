package com.ses.app.zxlauncher.model.zxdb

import com.ses.app.zxlauncher.model.Model
import com.ses.app.zxlauncher.model.ModelDownload
import com.ses.zxdb.*
import com.ses.zxdb.dao.Download
import com.ses.zxdb.dao.Extension
import com.ses.zxdb.dao.FileType
import java.io.File
import java.nio.file.Path

class ZXDBModelDownload(model: Model, private val download: Download) : ModelDownload(model) {
    // file_link = "/pub/sinclair/games/a/AcroJet.tzx.zip"
    // path = "wos/games/a/AcroJet.tzx.zip"
    /**
     * Obtiene la ruta al fichero local (relativa a la biblioteca) eliminado el prefijo del servidor y añadiendo su "id":
     *
     * file_link = "/pub/sinclair/games/a/AcroJet.tzx.zip"
     *
     * path = "wos/games/a/AcroJet.tzx.zip"
     */
    override fun getFilePath(): String {
        val server = download.downloadServer!!
        return "${server.id}/${File.separatorChar}${download.file_link.removePrefix(server.prefix)}"
    }
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