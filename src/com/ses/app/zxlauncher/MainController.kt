package com.ses.app.zxlauncher

import com.ses.app.zxlauncher.filters.EntryTitleFilter
import com.ses.app.zxlauncher.filters.Filter
import com.ses.app.zxlauncher.model.EntryRow
import com.ses.app.zxlauncher.model.Model
import com.ses.app.zxlauncher.model.ReleaseDate
import com.ses.app.zxlauncher.ui.ProgressDialog
import com.ses.zxdb.*
import com.ses.zxdb.dao.*
import javafx.application.Platform
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.ReadOnlyStringWrapper
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.ContextMenuEvent
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.stage.WindowEvent
import javafx.util.Callback
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URI
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class MainController : Initializable {
    companion object {
        fun load(): Parent {
            val loader = fxmlLoader("main.fxml")
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

    @FXML
    lateinit var previewImage: ImageView
    private val selectedImage = SimpleObjectProperty<Image>()

    @FXML
    lateinit var statusLabel: Label

    private var filters: ArrayList<Filter<EntryRow>> = ArrayList()
    private val downloadManager = DownloadManager()

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        initObservers()

        App.mainStage.addEventFilter(WindowEvent.WINDOW_SHOWN) { ev ->
            if (ZXDB.open()) {
                initModels()
            } else {
                if (Alert(Alert.AlertType.CONFIRMATION).apply {
                            title = T("error")
                            headerText = T("no_database_found")
                            contentText = T("download_database_q")
                        }.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                    updateZXDB()
                }
            }
        }
    }

    private fun initModels() {
        val dialog = ProgressDialog.create().apply {
            title = T("loading")
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

            statusLabel.text = T("items_count_fmt").format(tableView.items.size)
        }

        // al seleccionar un elemento de la lista, actualizar la lista de descargas
        tableView.selectionModel.selectedItemProperty().addListener { _, _, entry ->
            if (entry != null) {
                downloadsTableView.items.setAll(entry.downloads)
            } else {
                downloadsTableView.items.clear()
            }
        }

        downloadsTableView.selectionModel.selectedItemProperty().addListener { _, _, download ->
            when (download?.extension?.ext) {
                ".bmp", ".gif", ".jpg", ".png" -> if (downloadManager.exists(download)) {
                    val file = downloadManager.getFile(download)
                    selectedImage.value = file.toImage()
                }
                else -> selectedImage.value = null
            }
        }

        // "truco" para hacer que la imagen crezca
        previewImage.fitWidthProperty().bind((previewImage.parent as Region).widthProperty())
        previewImage.fitHeightProperty().bind((previewImage.parent as Region).heightProperty())
        previewImage.imageProperty().bind(selectedImage)
    }

    private fun createTree() {
        treeView.root = TreeGenreItem("ZXDB")
        treeView.root.isExpanded = true

        // crear los nodos en el orden de las categorías
        ZXDB.getTable(GenreType::class).rows.forEach { getCategoryNode(it.text) }

        // year
        val yearNode = getTreeNode(T("year"))

        // machine
        ZXDB.getTable(MachineType::class).rows.forEach { getTreeNode(listOf(T("machine"), it.text)) }

        // availability
        ZXDB.getTable(AvailableType::class).rows.forEach { getTreeNode(listOf(T("availability"), it.text)) }

        // añadir las entradas a los nodos
        Model.entryRows.forEach { addTreeEntry(it) }

        // ordenar años
        yearNode.children.sortBy { it.value }
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
        addTreeEntry(entry, listOf(T("year"), entry.releaseYearString), true)
        addTreeEntry(entry, listOf(T("availability"), entry.availabilityString))
        if (entry.machineTypeId != null) addTreeEntry(entry, listOf(T("machine"), entry.machineTypeString))
    }

    private fun addTreeEntry(entry: EntryRow, path: List<String>, sortNodes: Boolean = false) {
        val p = ArrayList<String>()
        for (s in path) {
            p.add(s)
            getTreeNode(p).addEntry(entry)
        }
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

            add(TableColumn<EntryRow, String>(T("title")).apply {
                cellValueFactory = Callback { p -> ReadOnlyStringWrapper(p.value.title) }
            })
            add(TableColumn<EntryRow, String>(T("genre")).apply {
                cellValueFactory = Callback { p -> ReadOnlyStringWrapper(p.value.categoryName) }
            })
            add(TableColumn<EntryRow, ReleaseDate>(T("date")).apply {
                cellValueFactory = Callback { p -> ReadOnlyObjectWrapper(p.value.releaseDate) }
            })
            add(TableColumn<EntryRow, String>(T("machine")).apply {
                cellValueFactory = Callback { ReadOnlyStringWrapper(it.value.machineType?.text) }
            })
            add(TableColumn<EntryRow, String>(T("availability")).apply {
                cellValueFactory = Callback { p -> ReadOnlyStringWrapper(p.value.availabilityString) }
            })
        }
    }

    private fun createDownloadsTable() {
        with(downloadsTableView.columns) {
            clear()

            add(TableColumn<Download, String>(T("name")).apply {
                //cellValueFactory = Callback { ReadOnlyStringWrapper(it.value.fileName) }
                cellFactory = Callback { FileDownloadTableCell(downloadManager) }
            })

            add(TableColumn<Download, String>(T("type")).apply {
                cellValueFactory = Callback { ReadOnlyStringWrapper(it.value.fileType.text) }
            })

            /*
            add(TableColumn<Download, String>(T("format")).apply {
                cellValueFactory = Callback { ReadOnlyStringWrapper(it.value.extension?.text) }
            })
            */

            add(TableColumn<Download, String>(T("year")).apply {
                cellValueFactory = Callback { p -> ReadOnlyStringWrapper(p.value.release_year?.toString()) }
            })

            add(TableColumn<Download, String>(T("machine")).apply {
                cellValueFactory = Callback { ReadOnlyStringWrapper(it.value.machineType?.text) }
            })
        }

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
        val button = Alert(Alert.AlertType.CONFIRMATION).apply {
            title = T("update")
            headerText = T("download_database_warning")
            contentText = T("are_you_sure")
        }.showAndWait().orElse(ButtonType.CANCEL)

        if (button == ButtonType.OK) {
            updateZXDB()
        }
    }

    @FXML
    fun menuQuit() {
        Platform.exit()
    }

    private fun updateZXDB() {
        val dialog = ProgressDialog.create().apply {
            title = T("updating_database")
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
            title = T("about")
            headerText = FULL_APP_NAME
            contentText = null
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
            if (!downloadManager.exists(download)) {
                add(MenuItem(T("download")).apply {
                    setOnAction {
                        getDownload(download)
                    }
                })
            }

            // menú "abrir con..." con los programas soportados
            val list = Config.getPrograms(download)
            if (list.isNotEmpty()) {
                add(Menu(T("open_with_")).also { menu ->
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
        if (download.fileType.id == FileType.REMOTE_LINK) {
            try {
                java.awt.Desktop.getDesktop().browse(URI(download.file_link))
            } catch (e: Exception) {
                //
            }
            return
        }

        //println("getDownload: ${download.fileName}")
        downloadManager.download(download) { file ->
            downloadsTableView.refresh()
            if (download.isImage) selectedImage.value = file.toImage()
            program?.launch(file)
        }
    }
}

class FileDownloadTableCell(private val downloadManager: DownloadManager) : TableCell<Download, String>() {
    private val cloudImage = Image(javaClass.getResourceAsStream("/cloud.png"))
    private val downloadedImage = Image(javaClass.getResourceAsStream("/check.png"))

    private val iconView = ImageView()

    init {
        graphic = iconView
    }

    override fun updateItem(value: String?, empty: Boolean) {
        val download = tableRow?.item
        if (empty || (download == null)) {
            text = null
            iconView.image = null
        } else {
            text = download.fileName
            iconView.image = if (downloadManager.exists(download)) downloadedImage else cloudImage
        }
    }
}
