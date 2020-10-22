package com.ses.app.zxlauncher.model.local

import com.ses.app.zxlauncher.doubleExtension
import com.ses.app.zxlauncher.model.Model
import com.ses.app.zxlauncher.model.ModelDownload
import com.ses.zxdb.*
import com.ses.zxdb.dao.Download
import com.ses.zxdb.dao.Extension
import com.ses.zxdb.dao.FileType
import java.io.File
import java.nio.file.Path

class LocalModelDownload(model: Model, private val _file: File) : ModelDownload(model) {
    private val _extension: Extension = Extension().apply {
        ext = _file.doubleExtension
        text = ext //TODO: cambiar cuando esté "ModelExtension"
    }

    private val _fileType: FileType = FileType().apply {
        id = 8
        text = _extension.text
    }

    private val _isImage = false

    override fun getFilePath(): String = _file.relativeTo(model.dir).path
    override fun getFileName(): String = _file.name
    override fun getLink(): String = ""
    override fun getFullUrl(): String = ""
    override fun getExtension(): Extension? = _extension
    override fun getFileType(): FileType = _fileType
    override fun getFormat(): String? = _extension.text
    override fun getReleaseYear(): Int? = null
    override fun getMachine(): String? = null
    override fun isImage(): Boolean = _isImage
}