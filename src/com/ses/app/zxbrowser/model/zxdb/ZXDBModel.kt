package com.ses.app.zxbrowser.model.zxdb

import com.ses.app.zxbrowser.DownloadManager
import com.ses.app.zxbrowser.I
import com.ses.app.zxbrowser.T
import com.ses.app.zxbrowser.model.*
import com.ses.net.Http
import com.ses.zxdb.ZXDB
import com.ses.zxdb.converter.MySQLConverter
import com.ses.zxdb.dao.AvailableType
import com.ses.zxdb.dao.GenreType
import com.ses.zxdb.dao.MachineType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class ZXDBModel(name: String, dir: File) : Model(name, dir) {
    private val downloadManager = DownloadManager(dir)

    private val _entries by lazy {
        ArrayList<ZXDBModelEntry>().also { list ->
            list.clear()
            ZXDB.sql().select(ZXDBModelEntry::class) { entry ->
                entry.model = this
                list.add(entry)
            }
            list.sortBy { e -> e.getTitle() }
        }
    }

    override val root: TreeNode = TreeNode(name).apply {
        collapsedIcon = I("zxdb")
    }

    init {
        ZXDB.open()
    }

    override fun getTree(): TreeNode {
        createTree()

        return root
    }

    private fun createTree() {
        root.children.clear()

        if (ZXDB.isOpened) {
            // crear los nodos en el orden de las categorías
            ZXDB.getTable(GenreType::class).rows.forEach { getNode(root, getGenrePath(it)) }

            // year
            val yearNode = getNode(root, T("year"))

            // machine
            ZXDB.getTable(MachineType::class).rows.forEach { getNode(root, listOf(T("machine"), it.text)) }

            // availability
            ZXDB.getTable(AvailableType::class).rows.forEach { getNode(root, listOf(T("availability"), it.text)) }

            // añadir las entradas a los nodos
            for (row in _entries) addTreeEntry(root, row)

            // ordenar años
            yearNode.children.sortBy { it.value }
        }
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

    override fun getEntries(): List<ModelEntry> = _entries

    override fun canUpdate(): Boolean = true

    override fun needsUpdate(): Boolean = !ZXDB.isOpened

    private fun getGenrePath(genre: GenreType?): List<String> {
        return genre?.text
                ?.replace("(.*) Game:".toRegex(), "Game: \$1:")
                ?.split(": ?".toRegex())
                ?: listOf(NULL_GENRE_STRING)
    }

    private fun getCategoryPath(entry: ZXDBModelEntry): List<String> = getGenrePath(entry.genreType)

    /** Añade una entrada a los nodos correspondientes, creando los necesarios. */
    private fun addTreeEntry(root: TreeNode, entry: ZXDBModelEntry) {
        root.addEntry(entry)
        addTreeEntry(root, entry, getCategoryPath(entry))
        addTreeEntry(root, entry, listOf(T("year"), entry.releaseYearString))
        addTreeEntry(root, entry, listOf(T("availability"), entry.getAvailability()))
        if (entry.machineTypeId != null) addTreeEntry(root, entry, listOf(T("machine"), entry.getMachine()))
    }

    private fun addTreeEntry(root: TreeNode, entry: ZXDBModelEntry, path: List<String>) {
        val p = ArrayList<String>()
        for (s in path) {
            p.add(s)
            getNode(root, p).addEntry(entry)
        }
    }

    override fun update(progressHandler: UpdateProgressHandler?) {
        GlobalScope.launch {
            val workingDir = File(System.getProperty("user.dir"))
            val mySqlFile = File(workingDir, "ZXDB_mysql.sql")
            val sqliteFile = File(workingDir, ZXDB.DB_NAME)
            val sqliteTempFile = File(workingDir, "_${ZXDB.DB_NAME}_")

            var downloadComplete = false
            // descargar ZXDB_mysql.sql
            Http().apply {
                request = "https://github.com/zxdb/ZXDB/raw/master/ZXDB_mysql.sql"
                getFile(mySqlFile) { status, progress ->
                    when (status) {
                        Http.Status.Connecting -> progressHandler?.invoke(UpdateStatus.Connecting, 0.0f, T("connecting_"))
                        Http.Status.Connected -> progressHandler?.invoke(UpdateStatus.Downloading, 0.0f, T("downloading"))
                        Http.Status.Downloading -> progressHandler?.invoke(UpdateStatus.Downloading, progress, T("downloading"))
                        Http.Status.Completed -> downloadComplete = true
                        Http.Status.Error -> progressHandler?.invoke(UpdateStatus.Error, progress, "Error")
                    }
                }
            }

            if (!downloadComplete) return@launch

            // convertir a sqlite
            progressHandler?.invoke(UpdateStatus.Converting, 0.0f, T("converting"))
            MySQLConverter(mySqlFile.absolutePath, sqliteTempFile.absolutePath).convert { progress, tableName ->
                progressHandler?.invoke(UpdateStatus.Converting, progress, T("converting_table_fmt").format(tableName))
            }

            // sustituir fichero
            ZXDB.close()

            mySqlFile.delete()
            sqliteFile.delete()
            sqliteTempFile.renameTo(sqliteFile)

            // recargar base de datos y refrescar los datos
            ZXDB.open()

            progressHandler?.invoke(UpdateStatus.Completed, 1.0f, T("completed"))
        }
    }

    override fun isImage(download: ModelDownload?): Boolean = (download?.isImage() == true)
    override fun isDownloaded(download: ModelDownload): Boolean = downloadManager.exists(download)
    override fun getFile(download: ModelDownload): File = downloadManager.getFile(download)
    override fun download(download: ModelDownload, completion: (file: File) -> Unit) = downloadManager.download(download, completion)
}