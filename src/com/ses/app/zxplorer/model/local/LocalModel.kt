package com.ses.app.zxplorer.model.local

import com.ses.app.zxplorer.I
import com.ses.app.zxplorer.T
import com.ses.app.zxplorer.model.*
import java.io.File

class LocalModel(name: String, dir: File) : Model(name, dir) {
    private val _entries = LinkedHashMap<String, LocalModelEntry>()

    override val root = TreeNode(name).apply {
        collapsedIcon = I("computer")
    }

    private fun getFiles(d: File) {
        if (d.isDirectory) {
            d.listFiles()?.forEach { file ->
                if (file.isFile) {
                    val download = LocalModelDownload(this, file)
                    val key = download.nameExtractor.baseName
                    val entry = _entries.getOrPut(key) { LocalModelEntry(this) }
                    entry.addFile(download)
                } else {
                    getFiles(file)
                }
            }
        }
    }

    override fun getTree(): TreeNode {
        if (root.children.isEmpty()) {
            createTree()
        }
        return root
    }

    private fun createTree() {
        _entries.clear()
        getFiles(dir)
        root.children.clear()
        for (entry in _entries.values) {
            addTreeEntry(root, entry)
        }
    }

    private fun getCategoryPath(entry: LocalModelEntry): List<String> {
        val download = entry.getDownloads()[0]
        val parent = File(download.getFilePath()).parent
        return parent?.split(File.separatorChar) ?: emptyList()
    }

    /** Añade una entrada a los nodos correspondientes, creando los necesarios. */
    private fun addTreeEntry(root: TreeNode, entry: LocalModelEntry) {
        root.addEntry(entry)
        addTreeEntry(root, entry, getCategoryPath(entry))
    }

    private fun addTreeEntry(root: TreeNode, entry: LocalModelEntry, path: List<String>) {
        val p = ArrayList<String>()
        for (s in path) {
            p.add(s)
            root.getNode(p) {
                it.collapsedIcon = I("folder")
                it.expandedIcon = I("folder_open")
            }.addEntry(entry)
        }
    }


    override fun getEntries(): List<ModelEntry> = _entries.values.toList()

    override fun canUpdate(): Boolean = false

    override fun needsUpdate(): Boolean = false

    override fun update(progressHandler: UpdateProgressHandler?) {
        progressHandler?.invoke(UpdateStatus.Completed, 1.0f, T("completed"))
    }

    override fun isImage(download: ModelDownload?): Boolean = (download?.isImage() == true)

    override fun isDownloaded(download: ModelDownload): Boolean = true

    override fun getFile(download: ModelDownload): File = download.getFile()

    override fun download(download: ModelDownload, completion: (file: File?) -> Unit) {
        completion(getFile(download))
    }
}