package com.ses.app.zxlauncher.model.local

import com.ses.app.zxlauncher.T
import com.ses.app.zxlauncher.model.*
import java.io.File

class LocalModel(name: String, dir: File) : Model(name, dir) {
    override fun getTree(): TreeNode {
        val root = TreeNode(name)
        return root
    }

    override fun getEntries(): List<ModelEntry> {
        return emptyList()
    }

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
        return File("test.txz.zip")
    }

    override fun download(download: ModelDownload, completion: (file: File) -> Unit) {
        completion(getFile(download))
    }
}