package com.ses.app.zxbrowser

import com.ses.app.zxbrowser.filters.EntryTitleFilter
import com.ses.app.zxbrowser.filters.Filter
import com.ses.app.zxbrowser.model.*
import com.ses.app.zxbrowser.ui.EditProgramDialog
import com.ses.app.zxbrowser.ui.ProgressDialog
import com.ses.zxdb.dao.FileType
import javafx.application.Platform
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.ReadOnlyStringWrapper
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.ContextMenuEvent
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

    @FXML
    lateinit var menuLibraries: Menu

    // toolbar
    @FXML
    lateinit var searchTextField: TextField

    @FXML
    lateinit var searchRegExToggleButton: ToggleButton

    @FXML
    lateinit var treeView: TreeView<String>

    @FXML
    lateinit var tableView: TableView<ModelEntry>

    @FXML
    lateinit var tableStatusLabel: Label

    @FXML
    lateinit var downloadsTableView: TableView<ModelDownload>

    @FXML
    lateinit var downloadsStatusLabel: Label

    @FXML
    lateinit var previewImage: ImageView
    private val selectedImage = SimpleObjectProperty<Image>()

    @FXML
    lateinit var statusProgress: ProgressBar

    private val zxdbModel = Config.allLibraries.firstOrNull { it.type == "zxdb" }?.model

    private var filters: ArrayList<Filter<ModelEntry>> = ArrayList()

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        initUI()

        App.mainStage.addEventFilter(WindowEvent.WINDOW_SHOWN) {
            checkLibraries {
                initModels()
            }
        }
        /*
        App.mainStage.addEventFilter(WindowEvent.WINDOW_SHOWN) {
            if (zxdbModel != null) {
                if (ZXDB.open()) {
                    initModels()
                } else {
                    if (Alert(Alert.AlertType.CONFIRMATION).apply {
                                title = T("error")
                                headerText = T("no_database_found")
                                contentText = T("download_database_q")
                            }.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                        updateLibrary(zxdbModel)
                    } else {
                        initModels()
                    }
                }
            } else {
                initModels()
            }
        }
        */
    }

    private fun initMenu() {
        for (lib in Config.allLibraries) {
            val menu = Menu(lib.name)
            val op = MenuItem(T("update"))
            op.setOnAction {
                onUpdateLibraryOption(lib)
            }
            op.isDisable = !lib.model.canUpdate()
            menu.items.add(op)

            menuLibraries.items.add(menu)
        }
    }

    private fun initUI() {
        initMenu()

        createTree()
        createTable()
        createDownloadsTable()

        initObservers()
    }

    private fun initModels() {
        val dialog = ProgressDialog.create().apply {
            title = T("loading_")
            progress = ProgressBar.INDETERMINATE_PROGRESS
            show(App.mainStage)
        }

        ProgressManager.new { progress, status, message ->
            dialog.progress = progress
            dialog.message = message
        }

        val root: TreeNode = treeView.root as TreeNode

        root.children.clear()

        /*
        statusProgress.apply {
            isVisible = true
            progress = ProgressBar.INDETERMINATE_PROGRESS
        }
        */

        GlobalScope.launch {
            Config.allLibraries.forEach { lib ->
                ProgressManager.current?.notify(ProgressBar.INDETERMINATE_PROGRESS, null, "${lib.name}...")
                root.children.add(lib.model.root)
                lib.model.root.isExpanded = true
                val libTree = lib.model.getTree()
                libTree.isExpanded = true
                //root.children.add(libTree)

                if (Config.general.showRootNode) root.addEntries(libTree.entries)
            }

            if (Config.general.showRootNode) root.entries.sortBy { it.getTitle() }

            ProgressManager.current?.end()
            Platform.runLater {
                //statusProgress.isVisible = false
                dialog.hide()
            }
        }
    }

    /*
    private fun initModels() {
        val dialog = ProgressDialog.create().apply {
            title = T("loading_")
            progress = ProgressBar.INDETERMINATE_PROGRESS
            show(App.mainStage)
        }

        GlobalScope.launch {
            val rootNode = createTree()

            Platform.runLater {
                treeView.root = rootNode

                createTable()
                createDownloadsTable()

                selectTreeNode(treeView.root)

                dialog.hide()
            }
        }
    }
    */

    private fun initObservers() {
        // al seleccionar un nodo, actualizar la lista
        treeView.selectionModel.selectedItemProperty().addListener { _, _, item ->
            if (item != null) {
                val category = item as TreeNode
                tableView.items = filterList(category.entries, tableView.items)
            } else {
                tableView.items.clear()
            }

            //statusLabel.text = T("items_count_fmt").format(tableView.items.size)
        }

        // al seleccionar un elemento de la lista, actualizar la lista de descargas
        tableView.selectionModel.selectedItemProperty().addListener { _, _, entry ->
            if (entry != null) {
                downloadsTableView.items.setAll(entry.getDownloads())
            } else {
                downloadsTableView.items.clear()
            }
        }

        tableView.items.addListener(ListChangeListener {
            tableStatusLabel.text = T("items_count_fmt").format(tableView.items.size)
        })

        downloadsTableView.selectionModel.selectedItemProperty().addListener { _, _, download ->
            var image: Image? = null

            if (download != null) {
                val model = download.model
                if (model.isImage(download) && model.isDownloaded(download)) {
                    val file = model.getFile(download)
                    image = file.toImage()
                }
            }

            selectedImage.value = image
        }

        downloadsTableView.items.addListener(ListChangeListener {
            downloadsStatusLabel.text = T("items_count_fmt").format(downloadsTableView.items.size)
        })

        // "truco" para hacer que la imagen crezca
        previewImage.fitWidthProperty().bind((previewImage.parent as Region).widthProperty())
        previewImage.fitHeightProperty().bind((previewImage.parent as Region).heightProperty())
        previewImage.imageProperty().bind(selectedImage)
    }

    private fun createTree() {
        treeView.isShowRoot = Config.general.showRootNode

        val root = TreeNode(T("all"))
        root.isExpanded = true

        if (Config.general.showRootNode) root.entries.sortBy { it.getTitle() }

        treeView.root = root
    }

    /*
    private fun _createTree(): TreeNode {
        treeView.isShowRoot = Config.general.showRootNode

        val root = TreeNode(T("all"))
        root.isExpanded = true

        Config.allLibraries.forEach { lib ->
            val libTree = lib.model.getTree()
            libTree.isExpanded = true
            root.children.add(libTree)

            if (Config.general.showRootNode) root.addEntries(libTree.entries)
        }

        if (Config.general.showRootNode) root.entries.sortBy { it.getTitle() }

        return root
    }
    */

    private fun selectTreeNode(item: TreeItem<String>) {
        if (treeView.selectionModel.selectedItem != item) {
            treeView.selectionModel.select(item)
        } else {
            tableView.items = filterList((item as TreeNode).entries, tableView.items)
        }

        tableView.selectionModel.clearSelection()
    }

    private fun filterList(list: ObservableList<ModelEntry>, dest: ObservableList<ModelEntry>): ObservableList<ModelEntry> {
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

    private fun createTable() {
        with(tableView.columns) {
            clear()

            add(TableColumn<ModelEntry, String>(T("title")).apply {
                cellValueFactory = Callback { p -> ReadOnlyStringWrapper(p.value.getTitle()) }
            })
            add(TableColumn<ModelEntry, String>(T("genre")).apply {
                cellValueFactory = Callback { p -> ReadOnlyStringWrapper(p.value.getGenre()) }
            })
            add(TableColumn<ModelEntry, ReleaseDate>(T("date")).apply {
                cellValueFactory = Callback { p -> ReadOnlyObjectWrapper(p.value.getReleaseDate()) }
            })
            add(TableColumn<ModelEntry, String>(T("machine")).apply {
                cellValueFactory = Callback { ReadOnlyStringWrapper(it.value.getMachine()) }
            })
            add(TableColumn<ModelEntry, String>(T("availability")).apply {
                cellValueFactory = Callback { p -> ReadOnlyStringWrapper(p.value.getAvailability()) }
            })
        }
    }

    private fun createDownloadsTable() {
        with(downloadsTableView.columns) {
            clear()

            add(TableColumn<ModelDownload, String>(T("·")).apply {
                cellFactory = Callback { FileDownloadTableCell() }
            })

            add(TableColumn<ModelDownload, String>(T("name")).apply {
                cellValueFactory = Callback { ReadOnlyStringWrapper(it.value.getFileName()) }
                //cellFactory = Callback { FileDownloadTableCell(downloadManager) }
            })

            add(TableColumn<ModelDownload, String>(T("type")).apply {
                cellValueFactory = Callback { ReadOnlyStringWrapper(it.value.getFileType().text) }
            })

            /*
            add(TableColumn<Download, String>(T("format")).apply {
                cellValueFactory = Callback { ReadOnlyStringWrapper(it.value.extension?.text) }
            })
            */

            add(TableColumn<ModelDownload, String>(T("year")).apply {
                cellValueFactory = Callback { p -> ReadOnlyStringWrapper(p.value.getReleaseYear()?.toString()) }
            })

            add(TableColumn<ModelDownload, String>(T("machine")).apply {
                cellValueFactory = Callback { ReadOnlyStringWrapper(it.value.getMachine()) }
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

        treeView.selectionModel.selectedItem?.also {
            selectTreeNode(it)
        }
    }

    /*
    @FXML
    fun menuUpdateDatabaseAction() {
        if (zxdbModel == null) return
        val button = Alert(Alert.AlertType.CONFIRMATION).apply {
            title = T("update")
            headerText = T("download_database_warning")
            contentText = T("are_you_sure")
        }.showAndWait().orElse(ButtonType.CANCEL)

        if (button == ButtonType.OK) {
            updateLibrary(zxdbModel)
        }
    }
    */

    @FXML
    fun menuQuit() {
        Platform.exit()
    }

    @FXML
    fun menuConfigurePrograms() {
        EditProgramDialog.create().show(App.mainStage)
    }

    private fun checkLibraries(whenFinish: (() -> Unit)? = null) {
        checkNextLibrary(Config.allLibraries.toList(), 0, whenFinish)
    }

    private fun checkNextLibrary(list: List<Library>, index: Int, whenFinish: (() -> Unit)? = null) {
        if (index < list.size) {
            val lib = list[index]
            if (lib.model.needsUpdate()) updateLibrary(lib) {
                checkNextLibrary(list, index + 1, whenFinish)
            } else {
                checkNextLibrary(list, index + 1, whenFinish)
            }
        } else {
            whenFinish?.invoke()
        }
    }

    private fun onUpdateLibraryOption(lib: Library) {
        val button = Alert(Alert.AlertType.CONFIRMATION).apply {
            title = T("update")
            headerText = T("update_library_warning_fmt").format(lib.name)
            contentText = T("are_you_sure")
        }.showAndWait().orElse(ButtonType.CANCEL)

        if (button == ButtonType.OK) {
            updateLibrary(lib)
        }
    }

    private fun updateLibrary(lib: Library, whenFinish: (() -> Unit)? = null) {
        val dialog = ProgressDialog.create().apply {
            title = T("updating_library_fmt").format(lib.name)
            show()
        }

        lib.model.update { status, progress, message ->
            when (status) {
                Model.UpdateStatus.Connecting -> Platform.runLater {
                    dialog.progress = ProgressBar.INDETERMINATE_PROGRESS
                    dialog.message = message
                }
                Model.UpdateStatus.Completed -> Platform.runLater {
                    //initModels()
                    dialog.hide()
                    whenFinish?.invoke()
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
            title = T("about_")
            headerText = FULL_APP_NAME
            contentText = "Programado por sés"
        }.showAndWait()
    }

    @FXML
    fun onSearchTextChanged() {
        setTextFilter(searchTextField.text)
    }

    @FXML
    fun onDownloadsTableRowClick(e: MouseEvent) {
        val download = downloadsTableView.selectionModel.selectedItem
        if ((download != null) && (e.clickCount == 2)) {
            getDownload(download, Config.getDefaultProgram(download))
        }
    }

    @FXML
    fun onTreeContextMenuRequested(e: ContextMenuEvent) {
        //treeView.contextMenu.show(treeView, e.screenX, e.screenY)
    }

    @FXML
    fun onDownloadsTableContextMenuRequested(e: ContextMenuEvent) {
        val download = downloadsTableView.selectionModel.selectedItem ?: return

        downloadsTableView.contextMenu.items.apply {
            clear()

            val model = download.model

            // opción descargar
            if (!model.isDownloaded(download)) {
                add(MenuItem(T("download")).apply {
                    setOnAction {
                        getDownload(download)
                    }
                })
            }

            // menú "abrir con..." con los programas soportados
            val list = Config.getPrograms(download)
            //if (list.isNotEmpty()) {
                add(Menu(T("open_with_")).also { menu ->
                    list.forEach { program ->
                        menu.items.add(MenuItem(program.name).apply {
                            setOnAction {
                                getDownload(download, program)
                            }
                        })
                    }
                    menu.items.add(MenuItem(T("configure_programs")).apply {
                        setOnAction {
                            EditProgramDialog.create().show(App.mainStage)
                        }
                    })
                })
            //}
        }

        downloadsTableView.contextMenu.show(downloadsTableView, e.screenX, e.screenY)
    }

    private fun getDownload(download: ModelDownload, program: Program? = null) {
        if (download.getFileType().id == FileType.REMOTE_LINK) {
            try {
                java.awt.Desktop.getDesktop().browse(URI(download.getLink()))
            } catch (e: Exception) {
                //
            }
            return
        }

        //println("getDownload: ${download.fileName}")
        val model = download.model
        model.download(download) { file ->
            downloadsTableView.refresh()
            if (download.isImage()) selectedImage.value = file.toImage()
            // runLater para capturar bien la excepción y mostrar la ventana de error
            Platform.runLater { program?.launch(file) }
        }
    }
}

class FileDownloadTableCell : TableCell<ModelDownload, String>() {
    private val cloudImage = I("cloud")
    private val downloadedImage = I("file")

    private val iconView = ImageView()

    init {
        graphic = iconView
    }

    override fun updateItem(value: String?, empty: Boolean) {
        val download = tableRow?.item
        if (empty || (download == null)) {
            //text = null
            iconView.image = null
        } else {
            //text = download.fileName
            val model = download.model
            iconView.image = if (model.isDownloaded(download)) downloadedImage else cloudImage
        }
    }
}
