package com.ses.app.zxlauncher

import com.ses.app.zxlauncher.filters.EntryTitleFilter
import com.ses.app.zxlauncher.filters.Filter
import com.ses.app.zxlauncher.ui.ProgressDialog
import com.ses.net.Http
import com.ses.zxdb.*
import com.ses.zxdb.dao.Download
import com.ses.zxdb.dao.Entry
import com.ses.zxdb.dao.GenreType
import javafx.beans.Observable
import javafx.beans.property.ReadOnlyStringWrapper
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.control.*
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import java.io.File
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class MainController : Initializable {
    companion object {
        fun load(): Parent {
            val loader = FXMLLoader(MainController::class.java.getResource("main.fxml"))
            return loader.load()
        }
    }

    // toolbar
    @FXML
    lateinit var searchTextField: TextField

    @FXML
    lateinit var searchRegExToggleButton: ToggleButton

    @FXML
    lateinit var treeView: TreeView<String>

    @FXML
    lateinit var tableView: TableView<Entry>

    @FXML
    lateinit var downloadsTableView: TableView<Download>

    private var filters: ArrayList<Filter<Entry>> = ArrayList()
    private val downloadManager = DownloadManager()

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        createTree()
        createTable()
        createDownloadsTable()

        treeView.selectionModel.select(treeView.root)
    }

    private fun createTree() {
        treeView.root = TreeGenreItem("ZXDB")
        treeView.root.isExpanded = true

        // crear los nodos en el orden de las categorías
        ZXDB.getTable(GenreType::class).rows.forEach { genre ->
            getCategoryNode(genre.text!!)
        }

        // añadir las entradas a los nodos
        ZXDB.getTable(Entry::class).rows.forEach { entry ->
            addTreeEntry(entry)
        }
    }

    private fun selectTreeNode(item: TreeItem<String>) {
        if (treeView.selectionModel.selectedItem != item) {
            treeView.selectionModel.select(item)
        } else {
            tableView.items = filteredList((item as TreeGenreItem).entries)
        }

        tableView.selectionModel.clearSelection()
    }

    private fun filteredList(list: ObservableList<Entry>): FilteredList<Entry> = list.filtered {
        var pass = true
        for (f in filters) {
            if (!f.check(it)) {
                pass = false
                break
            }
        }
        pass
    }

    /** Añade una entrada a los nodos correspondientes, creando los necesarios. */
    private fun addTreeEntry(entry: Entry) {
        val path = ZXDBUtil.getCategoryPath(entry)

        var node = treeView.root as TreeGenreItem
        node.addEntry(entry)

        path.forEach { pathPart ->
            val n = node.children.find { item -> item.value == pathPart }

            if (n != null) {
                node = n as TreeGenreItem
            } else {
                TreeGenreItem(pathPart).also { cat ->
                    node.children.add(cat)
                    //node.children.sortBy { item -> item.value }
                    node = cat
                }
            }

            node.addEntry(entry)
        }
    }

    /** Obtiene un nodo de una categoría, creando los necesarios. */
    private fun getCategoryNode(name: String): TreeGenreItem {
        val path: List<String> = ZXDBUtil.getCategoryPath(name)
        var node = treeView.root

        path.forEach { pathPart ->
            val n = node.children.find { item -> item.value == pathPart }

            if (n != null) {
                node = n
            } else {
                TreeGenreItem(pathPart).also { cat ->
                    node.children.add(cat)
                    //node.children.sortBy { item -> item.value }
                    node = cat
                }
            }
        }

        return node as TreeGenreItem
    }

    private fun createTable() {
        tableView.addColumn<Entry, String>("Title") { ReadOnlyStringWrapper(it.value.title) }
        tableView.addColumn<Entry, String>("Category") { ReadOnlyStringWrapper(it.value.genre?.text) }

        treeView.selectionModel.selectedItemProperty().addListener { _, _, item ->
            val category = item as TreeGenreItem
            tableView.items = filteredList(category.entries)
        }
    }

    private fun createDownloadsTable() {
        downloadsTableView.addColumn<Download, String>("D") { ReadOnlyStringWrapper(downloadManager.exists(it.value).toString()) }
        downloadsTableView.addColumn<Download, String>("Name") { ReadOnlyStringWrapper(it.value.fileName) }
        downloadsTableView.addColumn<Download, String>("Type") { ReadOnlyStringWrapper(it.value.fileType?.text) }
        downloadsTableView.addColumn<Download, String>("Format") { ReadOnlyStringWrapper(it.value.formatType?.text) }
        downloadsTableView.addColumn<Download, String>("Machine") { ReadOnlyStringWrapper(it.value.machineType?.text) }

        downloadsTableView.items = FXCollections.observableArrayList()

        tableView.selectionModel.selectedItemProperty().addListener { _, _, entry ->
            if (entry != null) {
                downloadsTableView.items.setAll(entry.downloads)
                println(entry.title)
            } else {
                downloadsTableView.items.clear()
            }
        }
    }

    private fun setTextFilter(exp: String?) {
        if (exp != null) {
            var f = filters.filterIsInstance<EntryTitleFilter>().firstOrNull()
            if (f == null) {
                f = EntryTitleFilter(exp)
                filters.add(f)
            } else {
                f.text = exp
            }
        } else {
            filters.removeAll { it is EntryTitleFilter }
        }

        selectTreeNode(treeView.selectionModel.selectedItem)
    }

    @FXML
    fun menuUpdateDatabaseAction() {
        if (true) {
            ProgressDialog.create().show()
            return
        }

        val workingDir = File(System.getProperty("user.dir"))
        val file = File(workingDir, "ZXDB_mysql.sql")

        // descargar ZXDB_mysql.sql
        Http().apply {
            request = "https://github.com/zxdb/ZXDB/raw/master/ZXDB_mysql.sql"
            getFile(file) { status, progress ->
            }
        }

        // convertir a sqlite

        // sustituir fichero

        // recargar base de datos y refrescar los datos
    }

    @FXML
    fun menuAboutAction() {
        Alert(Alert.AlertType.INFORMATION).apply {
            title = "About"
            headerText = null
            contentText = "ZXLauncher 0.001"
        }.showAndWait()
    }

    @FXML
    fun onSearchTextChanged(e: KeyEvent) {
        setTextFilter(searchTextField.text)
    }

    @FXML
    fun onTableRowClick(e: MouseEvent) {
        //val entry = tableView.selectionModel.selectedItem
        //downloadsTableView.items.setAll(entry.downloads)
    }

    @FXML
    fun onDatabaseTableRowClick(e: MouseEvent) {
        val download = downloadsTableView.selectionModel.selectedItem
        if ((download != null) && (e.clickCount == 2)) {
            downloadManager.download(download) {
            }
        }
    }
}
