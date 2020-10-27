package com.ses.app.zxbrowser.model

import java.io.File

typealias UpdateProgressHandler = (status: Model.UpdateStatus, progress: Float, message: String) -> Unit

/*
Otras bases de datos:
 - http://www.bbcmicro.co.uk/about.php -> https://github.com/pau1ie/bbcmicro.co.uk
 - C64
    - FTP: https://www.lemon64.com/
*/

abstract class Model(val name: String, val dir: File) {
    companion object {
        const val NULL_YEAR_STRING = "?"
        const val NULL_GENRE_STRING = "Uncategorized"
        const val NULL_AVAILABLE_STRING = "Unknown"
        const val NULL_MACHINE_TYPE_STRING = "None"
    }

    enum class UpdateStatus {
        Connecting,
        Downloading,
        Converting,
        Completed,
        Error
    }

    abstract fun getTree() : TreeNode

    abstract fun getEntries(): List<ModelEntry>

    abstract fun updateDatabase(progressHandler: UpdateProgressHandler?)

    abstract fun isImage(download: ModelDownload?): Boolean
    abstract fun isDownloaded(download: ModelDownload): Boolean
    abstract fun getFile(download: ModelDownload): File
    abstract fun download(download: ModelDownload, completion: (file: File) -> Unit)
}