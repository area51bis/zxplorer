package com.ses.app.zxlauncher.model

import com.ses.zxdb.dao.Extension
import com.ses.zxdb.dao.FileType
import java.io.File

//TODO ¿cambiar Extension y FileType por tipos externos a ZXDB?
abstract class ModelDownload() {
    lateinit var model: Model

    constructor(model: Model) : this() {
        this.model = model
    }

    fun getFile(): File = File(model.dir, getFilePath())

    /** ruta relativa a la biblioteca/modelo */
    abstract fun getFilePath(): String
    abstract fun getFileName(): String
    abstract fun getLink(): String
    abstract fun getFullUrl(): String
    abstract fun getExtension(): Extension?
    abstract fun getFileType(): FileType
    abstract fun getFormat(): String?
    abstract fun getReleaseYear(): Int?
    abstract fun getMachine(): String?
    abstract fun isImage(): Boolean
}