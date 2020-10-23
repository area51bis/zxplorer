package com.ses.app.zxlauncher.model.local

import com.ses.app.zxlauncher.T
import com.ses.app.zxlauncher.model.*
import com.ses.app.zxlauncher.model.zxdb.ZXDBModelEntry
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
        for (entry in _entries.values) {
            addTreeEntry(root, entry)
        }
        //root.addEntries(_entries.values)
        return root
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
            root.getNode(p).addEntry(entry)
        }
    }


    override fun getEntries(): List<ModelEntry> = _entries.values.toList()

    override fun updateDatabase(progressHandler: UpdateProgressHandler?) {
        progressHandler?.invoke(UpdateStatus.Completed, 1.0f, T("completed"))
    }

    override fun isImage(download: ModelDownload?): Boolean = (download?.isImage() == true)

    override fun isDownloaded(download: ModelDownload): Boolean = true

    override fun getFile(download: ModelDownload): File = download.getFile()

    override fun download(download: ModelDownload, completion: (file: File) -> Unit) {
        completion(getFile(download))
    }
}