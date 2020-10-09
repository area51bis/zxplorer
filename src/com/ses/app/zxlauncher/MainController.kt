package com.ses.app.zxlauncher

import com.ses.app.zxlauncher.filters.EntryTitleFilter
import com.ses.app.zxlauncher.filters.Filter
import com.ses.app.zxlauncher.model.EntryRow
import com.ses.app.zxlauncher.model.Model
import com.ses.app.zxlauncher.ui.ProgressDialog
import com.ses.zxdb.*
import com.ses.zxdb.dao.AvailableType
import com.ses.zxdb.dao.Download
import com.ses.zxdb.dao.GenreType
import com.ses.zxdb.dao.MachineType
import javafx.application.Platform
import javafx.beans.property.ReadOnlyIntegerProperty
import javafx.beans.property.ReadOnlyIntegerWrapper
import javafx.beans.property.ReadOnlyStringWrapper
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
import javafx.util.Callback
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    lateinit var tableView: TableView<EntryRow>

    @FXML
    lateinit var downloadsTableView: TableView<Download>

    private var filters: ArrayList<Filter<EntryRow>> = ArrayList()
    private val downloadManager = DownloadManager()

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        initObservers()

        App.mainStage.addEventFilter(WindowEvent.WINDOW_SHOWN) { ev ->
            if (ZXDB.open()) {
                initModels()
            } else {
                updateZXDB()
            }
        }
    }

    private fun initModels() {
        val dialog = ProgressDialog.create().apply {
            title = "Loading..."
            progress = ProgressBar.INDETERMINATE_PROGRESS
            show()
        }

        GlobalScope.launch {
            Model.entryRows

            Platform.runLater {
                createTree()
                createTable()
                createDownloadsTable()

                selectTreeNode(treeView.root)

                dialog.hide()
            }
        }
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
        ZXDB.getTable(GenreType::class).rows.forEach { getCategoryNode(it.text) }

        // year
        getTreeNode("Year")

        // machine
        ZXDB.getTable(MachineType::class).rows.forEach { getTreeNode(listOf("Machine", it.text)) }

        // availability
        ZXDB.getTable(AvailableType::class).rows.forEach { getTreeNode(listOf("Availability", it.text)) }

        // añadir las entradas a los nodos
        Model.entryRows.forEach { addTreeEntry(it) }
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
    private fun filterList(list: ObservableList<EntryRow>, dest: ObservableList<EntryRow>): ObservableList<EntryRow> {
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
    private fun addTreeEntry(entry: EntryRow) {
        (treeView.root as TreeGenreItem).addEntry(entry)
        addTreeEntry(entry, entry.categoryPath)
        addTreeEntry(entry, listOf("Year", entry.releaseYearString), true)
        addTreeEntry(entry, listOf("Availability", entry.availabilityString))
        if (entry.machineTypeId != null) addTreeEntry(entry, listOf("Machine", entry.machineTypeString))
    }

    private fun addTreeEntry(entry: EntryRow, path: List<String>, sortNodes: Boolean = false) {
        getTreeNode(path, sortNodes).addEntry(entry)
    }

    /** Obtiene un nodo de una categoría, creando los necesarios. */
    private fun getCategoryNode(name: String): TreeGenreItem {
        return getTreeNode(Model.getCategoryPath(name))
    }

    private fun getTreeNode(path: String, sortNodes: Boolean = false): TreeGenreItem {
        return getTreeNode(path.split("|"), sortNodes)
    }

    private fun getTreeNode(path: List<String>, sortNodes: Boolean = false): TreeGenreItem {
        var node = treeView.root

        path.forEach { pathPart ->
            val n = node.children.find { item -> item.value == pathPart }

            if (n != null) {
                node = n
            } else {
                TreeGenreItem(pathPart).also { cat ->
                    node.children.add(cat)
                    if (sortNodes) node.children.sortBy { item -> item.value }
                    node = cat
                }
            }
        }

        return node as TreeGenreItem
    }

    private fun createTable() {
        with(tableView.columns) {
            clear()

            add(TableColumn<EntryRow, String>("Title").apply {
                cellValueFactory = Callback { p -> ReadOnlyStringWrapper(p.value.title) }
            })
            add(TableColumn<EntryRow, String>("Category").apply {
                cellValueFactory = Callback { p -> ReadOnlyStringWrapper(p.value.categoryName) }
            })
            add(TableColumn<EntryRow, String>("Year").apply {
                cellValueFactory = Callback { p -> ReadOnlyStringWrapper(p.value.releaseYearString) }
            })
            add(TableColumn<EntryRow, String>("Availability").apply {
                cellValueFactory = Callback { p -> ReadOnlyStringWrapper(p.value.availabilityString) }
            })
        }
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

        Model.updateDatabase { status, progress, message ->
            when (status) {
                Model.UpdateStatus.Connecting -> Platform.runLater {
                    dialog.progress = ProgressBar.INDETERMINATE_PROGRESS
                    dialog.message = message
                }
                //ZXDBUtil.UpdateStatus.Downloading -> TODO()
                //ZXDBUtil.UpdateStatus.Converting -> TODO()
                Model.UpdateStatus.Completed -> Platform.runLater {
                    initModels()
                    dialog.hide()
                }

                Model.UpdateStatus.Error -> Platform.runLater { dialog.hide() }

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
