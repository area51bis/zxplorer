package com.ses.app.zxlauncher.model.local

import com.ses.app.zxlauncher.model.Model
import com.ses.app.zxlauncher.model.ModelDownload
import com.ses.app.zxlauncher.model.ModelFileExtension
import com.ses.zxdb.dao.Extension
import com.ses.zxdb.dao.FileType
import java.io.File

class LocalModelDownload(model: Model, file: File) : ModelDownload(model) {
    private val _file: File = file.relativeTo(model.dir)

    private val _extension = ModelFileExtension(getFile())

    private val _zxdbExtension = Extension().apply {
        ext = _extension.doubleExtension
        text = _extension.rawExtension.toUpperCase()
    }

    private val _fileType = FileType().apply {
        id = 8
        text = _zxdbExtension.text
    }

    override fun getFilePath(): String = _file.path
    override fun getFileName(): String = _file.name
    override fun getLink(): String = ""
    override fun getFullUrl(): String = ""
    override fun getExtension(): Extension? = _zxdbExtension
    override fun getRawExtension(): String = _extension.rawExtension
    override fun getFileType(): FileType = _fileType
    override fun getFormat(): String? = _zxdbExtension.text
    override fun getReleaseYear(): Int? = null
    override fun getMachine(): String? = null
    override fun isImage(): Boolean = _extension.isImage
}