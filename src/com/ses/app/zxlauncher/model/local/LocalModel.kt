package com.ses.app.zxlauncher.model.local

import com.ses.app.zxlauncher.T
import com.ses.app.zxlauncher.model.*
import java.io.File

class LocalModel(name: String, dir: File) : Model(name, dir) {
    private val _entries = LinkedHashMap<String, LocalModelEntry>()

    init {
        getFiles(dir)
    }

    private fun getFiles(d: File) {
        if (d.isDirectory) {
            d.listFiles()?.forEach { file ->
                if (file.isFile) {
                    val key = LocalModelEntry.getFileKey(file)
                    val entry = _entries.getOrPut(key) { LocalModelEntry(this) }
                    entry.addFile(file)
                } else {
                    getFiles(file)
                }
            }
        }
    }

    override fun getTree(): TreeNode {
        val root = TreeNode(name)
        root.addEntries(_entries.values)
        return root
    }

    override fun getEntries(): List<ModelEntry> = _entries.values.toList()

    override fun updateDatabase(progressHandler: UpdateProgressHandler?) {
        progressHandler?.invoke(UpdateStatus.Completed, 1.0f, T("completed"))
    }

    override fun isImage(download: ModelDownload?): Boolean {
        return false
    }

    override fun isDownloaded(download: ModelDownload): Boolean {
        return true
    }

    override fun getFile(download: ModelDownload): File {
        return download.getFile()
    }

    override fun download(download: ModelDownload, completion: (file: File) -> Unit) {
        completion(getFile(download))
    }
}