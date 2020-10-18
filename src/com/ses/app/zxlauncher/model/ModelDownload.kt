package com.ses.app.zxlauncher.model

import com.ses.zxdb.dao.Extension
import com.ses.zxdb.dao.FileType

//TODO ¿cambiar Extension y FileType por tipos externos a ZXDB?
interface ModelDownload {
    fun getFileName(): String
    fun getLink(): String
    fun getFullUrl(): String
    fun getExtension(): Extension?
    fun getFileType(): FileType
    fun getFormat(): String?
    fun getReleaseYear(): Int?
    fun getMachine(): String?
    fun isImage(): Boolean
}