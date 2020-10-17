package com.ses.app.zxlauncher.model

typealias UpdateProgressHandler = (status: Model.UpdateStatus, progress: Float, message: String) -> Unit

abstract class Model {
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

    val entryRows: List<EntryRow> by lazy { getRows() }

    abstract fun getTree() : TreeNode

    abstract fun getRows(): List<EntryRow>

    abstract fun updateDatabase(progressHandler: UpdateProgressHandler?)
}