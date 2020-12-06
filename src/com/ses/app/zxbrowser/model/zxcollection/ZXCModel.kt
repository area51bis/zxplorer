package com.ses.app.zxbrowser.model.zxcollection

import com.ses.app.zxbrowser.App
import com.ses.app.zxbrowser.T
import com.ses.app.zxbrowser.model.*
import java.io.File

class ZXCModel(name: String, dir: File, val source: String? = null) : Model(name, dir) {
    val file: File = App.localFile("$name.json")

    override val root: TreeNode
        get() = TODO("Not yet implemented")

    override fun getTree(): TreeNode {
        TODO("Not yet implemented")
    }

    override fun getEntries(): List<ModelEntry> {
        TODO("Not yet implemented")
    }

    override fun canUpdate(): Boolean = false

    override fun needsUpdate(): Boolean = false

    override fun update(progressHandler: UpdateProgressHandler?) {
        progressHandler?.invoke(UpdateStatus.Completed, 1.0f, T("completed"))
    }

    override fun isImage(download: ModelDownload?): Boolean = download?.isImage() == true

    override fun isDownloaded(download: ModelDownload): Boolean {
        TODO("Not yet implemented")
    }

    override fun getFile(download: ModelDownload): File {
        TODO("Not yet implemented")
    }

    override fun download(download: ModelDownload, completion: (file: File) -> Unit) {
        TODO("Not yet implemented")
    }
}