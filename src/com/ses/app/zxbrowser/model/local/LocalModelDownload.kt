package com.ses.app.zxbrowser.model.local

import com.ses.app.zxbrowser.model.Model
import com.ses.app.zxbrowser.model.ModelDownload
import com.ses.app.zxbrowser.model.ModelFileExtension
import com.ses.zxdb.dao.Extension
import com.ses.zxdb.dao.FileType
import java.io.File

class LocalModelDownload(model: Model, file: File) : ModelDownload(model) {
    private val _file: File = file.relativeTo(model.dir)

    val modelExtension by lazy { ModelFileExtension(getFile()) }
    val nameExtractor by lazy { NameExtractor(this) }

    private val _zxdbExtension = Extension().apply {
        ext = modelExtension.doubleExtension
        text = modelExtension.rawExtension.toUpperCase()
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
    override fun getRawExtension(): String = modelExtension.rawExtension
    override fun getFileType(): FileType = _fileType
    override fun getFormat(): String? = _zxdbExtension.text
    override fun getReleaseYear(): Int? = null
    override fun getMachine(): String? = null
    override fun isImage(): Boolean = modelExtension.isImage
}