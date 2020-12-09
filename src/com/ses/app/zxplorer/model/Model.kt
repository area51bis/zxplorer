package com.ses.app.zxplorer.model

import java.io.File

typealias UpdateProgressHandler = (status: Model.UpdateStatus, progress: Float, message: String) -> Unit

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

    abstract val root: TreeNode
    abstract fun getTree() : TreeNode

    abstract fun getEntries(): List<ModelEntry>

    abstract fun canUpdate(): Boolean
    abstract fun needsUpdate(): Boolean
    abstract fun update(progressHandler: UpdateProgressHandler?)

    abstract fun isImage(download: ModelDownload?): Boolean
    abstract fun isDownloaded(download: ModelDownload): Boolean
    abstract fun getFile(download: ModelDownload): File
    abstract fun download(download: ModelDownload, completion: (file: File?) -> Unit)
}