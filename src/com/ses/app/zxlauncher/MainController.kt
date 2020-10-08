package com.ses.app.zxlauncher

import com.ses.app.zxlauncher.filters.EntryTitleFilter
import com.ses.app.zxlauncher.filters.Filter
import com.ses.app.zxlauncher.ui.ProgressDialog
import com.ses.zxdb.*
import com.ses.zxdb.dao.Download
import com.ses.zxdb.dao.Entry
import com.ses.zxdb.dao.GenreType
import javafx.application.Platform
import javafx.beans.property.ReadOnlyStringWrapper
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.control.*
import javafx.scene.input.ContextMenuEvent
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.VBox
import javafx.stage.WindowEvent
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

    @FXML
    lateinit var rootView: VBox

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
        initObservers()

        App.mainStage.addEventFilter(WindowEvent.WINDOW_SHOWN) { ev ->
            if (ZXDB.open()) {
                initModels()
            } else {
                //updateZXDB()
            }
        }
    }

    private fun initModels() {
        createTree()
        createTable()
        createDownloadsTable()

        selectTreeNode(treeView.root)
    }

    private fun initObservers() {
        // al seleccionar un nodo, actualizar la lista
        treeView.selectionModel.selectedItemProperty().addListener { _, _, item ->
            if (item != null) {
                val category = item as TreeGenreItem
                tableView.items = filterList(category.entries, tableView.items)
            } else {
                tableView.items.clear()
            }
        }

        // al seleccionar un elemento de la lista, actualizar la lista de descargas
        tableView.selectionModel.selectedItemProperty().addListener { _, _, entry ->
            if (entry != null) {
                downloadsTableView.items.setAll(entry.downloads)
            } else {
                downloadsTableView.items.clear()
            }
        }
    }

    private fun createTree() {
        treeView.root = TreeGenreItem("ZXDB")
        treeView.root.isExpanded = true

        // crear los nodos en el orden de las categorías
        ZXDB.getTable(GenreType::class).rows.forEach { genre ->
            getCategoryNode(genre.text)
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
            tableView.items = filterList((item as TreeGenreItem).entries, tableView.items)
        }

        tableView.selectionModel.clearSelection()
    }

    /*
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
    */
    private fun filterList(list: ObservableList<Entry>, dest: ObservableList<Entry>): ObservableList<Entry> {
        return list.filterTo(dest.apply { clear() }) {
            var pass = true
            for (f in filters) {
                if (!f.check(it)) {
                    pass = false
                    break
                }
            }
            pass
        }
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
        tableView.columns.clear()
        tableView.addColumn<Entry, String>("Title") { ReadOnlyStringWrapper(it.value.title) }
        tableView.addColumn<Entry, String>("Category") { ReadOnlyStringWrapper(ZXDBUtil.getCategoryName(it.value.genre)) }
    }

    private fun createDownloadsTable() {
        downloadsTableView.columns.clear()
        downloadsTableView.addColumn<Download, String>("D") { ReadOnlyStringWrapper(downloadManager.exists(it.value).toString()) }
        downloadsTableView.addColumn<Download, String>("Name") { ReadOnlyStringWrapper(it.value.fileName) }
        downloadsTableView.addColumn<Download, String>("Type") { ReadOnlyStringWrapper(it.value.fileType.text) }
        downloadsTableView.addColumn<Download, String>("Format") { ReadOnlyStringWrapper(it.value.extension?.text) }
        downloadsTableView.addColumn<Download, String>("Machine") { ReadOnlyStringWrapper(it.value.machineType?.text) }

        downloadsTableView.items = FXCollections.observableArrayList()

        downloadsTableView.contextMenu = ContextMenu()
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
        updateZXDB()
    }

    private fun updateZXDB() {
        val dialog = ProgressDialog.create().apply {
            title = "Updating database"
            show()
        }

        ZXDBUtil.updateDatabase { status, progress, message ->
            when (status) {
                ZXDBUtil.UpdateStatus.Connecting -> Platform.runLater { dialog.progress = ProgressBar.INDETERMINATE_PROGRESS }
                //ZXDBUtil.UpdateStatus.Downloading -> TODO()
                //ZXDBUtil.UpdateStatus.Converting -> TODO()
                ZXDBUtil.UpdateStatus.Completed -> Platform.runLater {
                    initModels()
                    dialog.hide()
                }

                ZXDBUtil.UpdateStatus.Error -> Platform.runLater { dialog.hide() }

                else -> Platform.runLater {
                    dialog.progress = progress.toDouble()
                    dialog.message = message
                }
            }
        }
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
    fun onDownloadsTableRowClick(e: MouseEvent) {
        val download = downloadsTableView.selectionModel.selectedItem
        if ((download != null) && (e.clickCount == 2)) {
            getDownload(download, Config.getDefaultProgram(download))
        }
    }

    @FXML
    fun onDownloadsTableContextMenuRequested(e: ContextMenuEvent) {
        val download = downloadsTableView.selectionModel.selectedItem ?: return

        downloadsTableView.contextMenu.items.apply {
            clear()

            // opción descargar
            add(MenuItem("Download").apply {
                setOnAction {
                    getDownload(download)
                }
            })

            // menú "abrir con..." con los programas soportados
            val list = Config.getPrograms(download)
            if (list.isNotEmpty()) {
                add(Menu("Open with...").also { menu ->
                    list.forEach { program ->
                        menu.items.add(MenuItem(program.name).apply {
                            setOnAction {
                                getDownload(download, program)
                            }
                        })
                    }
                })
            }
        }
        downloadsTableView.contextMenu.show(downloadsTableView, e.screenX, e.screenY)
    }

    private fun getDownload(download: Download, program: Program? = null) {
        //println("getDownload: ${download.fileName}")
        downloadManager.download(download) { file ->
            if (program != null) {
                val extension = download.extension
                if (extension != null) {
                    //println("open with ${program.name}")
                    program.launch(file)
                }
            }
        }
    }
}
