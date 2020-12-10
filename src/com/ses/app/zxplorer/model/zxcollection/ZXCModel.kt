package com.ses.app.zxplorer.model.zxcollection

import com.ses.app.zxplorer.DownloadManager
import com.ses.app.zxplorer.I
import com.ses.app.zxplorer.T
import com.ses.app.zxplorer.model.*
import com.ses.app.zxplorer.zxcollection.Genre
import com.ses.app.zxplorer.zxcollection.ZXCollection
import java.io.File

class ZXCModel(name: String, dir: File, val source: String? = null) : Model(name, dir) {
    private val file: File = File(source)
    val zxc: ZXCollection = ZXCollection.loadCollection(file)
    private val downloadManager = DownloadManager()

    private val _entries by lazy {
        ArrayList<ZXCModelEntry>().also { list ->
            zxc.entries.forEach {
                list.add(ZXCModelEntry(this, it))
            }
        }
    }

    override val root: TreeNode = TreeNode(zxc.info.name).apply {
        collapsedIcon = I("zxc")
    }

    override fun getTree(): TreeNode {
        if (root.children.isEmpty()) {
            createTree()
        }

        return root
    }

    override fun getEntries(): List<ModelEntry> = _entries

    override fun canUpdate(): Boolean = false

    override fun needsUpdate(): Boolean = false

    override fun update(progressHandler: UpdateProgressHandler?) {
        progressHandler?.invoke(UpdateStatus.Completed, 1.0f, T("completed"))
    }

    override fun isImage(download: ModelDownload?): Boolean = download?.isImage() == true

    override fun isDownloaded(download: ModelDownload): Boolean = downloadManager.exists(download)
    override fun getFile(download: ModelDownload): File = download.getFile()
    override fun download(download: ModelDownload, completion: (file: File?) -> Unit) = downloadManager.download(download.getFullUrl(), download.getFile(), completion)

    private fun createTree() {
        root.children.clear()

        //_entries.forEach { getNode(root, getGenrePath(it)) }
        // crear los nodos en el orden de las categorías
        //ZXDB.getTable(GenreType::class).rows.forEach { getNode(root, getGenrePath(it)) }

        // year
        //val yearNode = getNode(root, T("year"))

        // machine
        //ZXDB.getTable(MachineType::class).rows.forEach { getNode(root, listOf(T("machine"), it.text)) }

        // availability
        //ZXDB.getTable(AvailableType::class).rows.forEach { getNode(root, listOf(T("availability"), it.text)) }

        // añadir las entradas a los nodos
        for (row in _entries) addTreeEntry(root, row)

        // ordenar años
        //yearNode.children.sortBy { it.value }
    }

    private fun getNode(parent: TreeNode, path: String): TreeNode {
        return parent.getNode(path) {
            it.collapsedIcon = I("folder")
            it.expandedIcon = I("folder_open")
        }
    }

    private fun getNode(parent: TreeNode, path: List<String>): TreeNode {
        return parent.getNode(path) {
            it.collapsedIcon = I("folder")
            it.expandedIcon = I("folder_open")
        }
    }

    private fun getGenrePath(genre: Genre?): List<String> {
        return genre?.text
                ?.replace("(.*) Game:".toRegex(), "Game: \$1:")
                ?.split(": ?".toRegex())
                ?: listOf(NULL_GENRE_STRING)
    }

    private fun getGenrePath(modelEntry: ZXCModelEntry): List<String> = getGenrePath(modelEntry.entry.genre)

    /** Añade una entrada a los nodos correspondientes, creando los necesarios. */
    private fun addTreeEntry(root: TreeNode, modelEntry: ZXCModelEntry) {
        root.addEntry(modelEntry)
        addTreeEntry(root, modelEntry, getGenrePath(modelEntry))
        addTreeEntry(root, modelEntry, listOf(T("year"), modelEntry.releaseYearString))
        //addTreeEntry(root, entry, listOf(T("availability"), entry.getAvailability()))
        //if (modelEntry.machineTypeId != null) addTreeEntry(root, modelEntry, listOf(T("machine"), modelEntry.getMachine()))
    }

    private fun addTreeEntry(root: TreeNode, entry: ZXCModelEntry, path: List<String>) {
        val p = ArrayList<String>()
        for (s in path) {
            p.add(s)
            getNode(root, p).addEntry(entry)
        }
    }
}