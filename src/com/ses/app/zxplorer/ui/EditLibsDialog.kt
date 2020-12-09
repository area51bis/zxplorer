package com.ses.app.zxplorer.ui

import com.ses.app.zxplorer.*
import com.ses.app.zxplorer.zxcollection.ZXCollection
import javafx.application.Platform
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.input.MouseEvent
import javafx.scene.layout.GridPane
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.io.File
import java.net.URL
import java.util.*

class EditLibsDialog : AppDialog<Boolean>() {
    @FXML
    lateinit var listView: ListView<Library>

    @FXML
    lateinit var editView: GridPane

    @FXML
    lateinit var typeText: TextField

    @FXML
    lateinit var nameText: TextField

    @FXML
    lateinit var pathText: TextField

    private lateinit var libList: ObservableList<Library>
    private var selectedLibrary: Library? = null

    private var _result = false

    companion object {
        private const val TYPE_LOCAL_ZXC = "local_zxc"
        private const val TYPE_REMOTE_ZXC = "remote_zxc"

        private val LIB_NAMES: Map<String, String> = mapOf(
            Library.TYPE_ZXDB to T("lib_type_zxdb"),
            Library.TYPE_LOCAL to T("lib_type_local"),
            Library.TYPE_ZXC to T("lib_type_zxc")
        )

        fun create() = create<EditLibsDialog>("edit_libs_dialog.fxml")
    }

    override fun createStage(): Stage = super.createStage().apply {
        initStyle(StageStyle.UTILITY)
        isResizable = false
    }

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        super.initialize(location, resources)
        stage.title = T("libraries")

        libList = listView.items
        Config.allLibraries.forEach { libList.add(it) }
        listView.selectionModel.selectedItemProperty().addListener { _, _, lib ->
            selectLibrary(lib)
        }

        nameText.textProperty().addListener { _, _, text -> selectedLibrary?.name = text; listView.refresh() }
        pathText.textProperty().addListener { _, _, text -> selectedLibrary?.path = text }

        updateLibInfo(null)
    }

    override fun getResult(): Boolean? = _result

    private fun selectLibrary(lib: Library) {
        selectedLibrary = null
        updateLibInfo(lib)
        selectedLibrary = lib
    }

    private fun updateLibInfo(lib: Library?) {
        editView.isDisable = (lib == null)
        if (lib != null) {
            typeText.text = LIB_NAMES[lib.type]
            nameText.text = lib.name
            pathText.text = lib.path
        } else {
            typeText.text = null
            nameText.text = null
            pathText.text = null
        }
    }

    private fun chooseDirectory(dir: File? = null): File? = DirectoryChooser().let { chooser ->
        chooser.title = T("select_dir")
        if (dir != null) chooser.initialDirectory = dir
        chooser.showDialog(stage)
    }

    private fun chooseFile(dir: File? = null): File? = FileChooser().let { fc ->
        fc.title = T("select_file")
        if (dir != null) fc.initialDirectory = dir
        fc.showOpenDialog(stage)
    }

    private val addContextMenu = ContextMenu().apply {
        items.addAll(
                MenuItem(T("lib_type_zxdb")).apply { setOnAction { addLibrary(Library.TYPE_ZXDB) } },
                MenuItem(T("lib_type_local")).apply { setOnAction { addLibrary(Library.TYPE_LOCAL) } },
                Menu(T("lib_type_zxc")).apply {
                    items.addAll(
                            MenuItem(T("local")).apply { setOnAction { addLibrary(TYPE_LOCAL_ZXC) } },
                            MenuItem(T("remote")).apply { setOnAction { addLibrary(TYPE_REMOTE_ZXC) } },
                    )
                }
        )
    }

    private fun addLibrary(type: String) {
        when (type) {
            Library.TYPE_ZXDB -> {
                val lib = Library(type, "ZXDB", "zxdb")
                addLib(lib)
            }

            Library.TYPE_LOCAL -> {
                val dir = chooseDirectory(App.workingDir)
                if (dir != null) {
                    val lib = Library(type, dir.name, dir.absolutePath)
                    addLib(lib)
                }
            }

            TYPE_LOCAL_ZXC -> {
                val file = chooseFile(App.workingDir)
                if (file != null) {
                    val info = ZXCollection.loadInfo(file)
                    val lib = Library(Library.TYPE_ZXC, info!!.name, file.nameWithoutExtension, file.absolutePath)
                    addLib(lib)
                }
            }

            TYPE_REMOTE_ZXC -> {
                val url = TextInputDialog().apply {
                    title = "${T("lib_type_zxc")} (${T("remote")})"
                    headerText = null
                    contentText = "URL"
                    dialogPane.prefWidth = 400.0
                }.showAndWait().orElse(null)

                if (url != null) {
                    val name = url.substringAfterLast('/').substringBefore('?')
                    val file = App.localFile(name)
                    DownloadManager().download(url, file) { f ->
                        if (f != null) {
                            try {
                                val info = ZXCollection.loadInfo(f)
                                if (info != null) {
                                    val lib = Library(Library.TYPE_ZXC, info.name, f.nameWithoutExtension, f.absolutePath)
                                    Platform.runLater { addLib(lib) }
                                }
                            } catch (e: Exception) {
                                f.delete()
                                Alert(Alert.AlertType.ERROR).apply {
                                    title = T("error")
                                    contentText = "Couldn't load collection"
                                }.show()
                            }
                        } else {
                            Alert(Alert.AlertType.ERROR).apply {
                                title = T("error")
                                contentText = "Download error"
                            }.show()
                        }
                    }
                }
            }
        }
    }

    private fun addLib(lib: Library) {
        libList.add(lib)
        listView.selectionModel.select(lib)
    }

    @FXML
    fun onAddClick(e: MouseEvent) {
        addContextMenu.show(e.source as Button, e.screenX, e.screenY)
    }

    @FXML
    fun onRemoveClick() {
        if (selectedLibrary != null) {
            val button = Alert(Alert.AlertType.CONFIRMATION).apply {
                title = T("warning")
                headerText = T("remove_library_q")
            }.showAndWait().orElse(ButtonType.CANCEL)

            if (button == ButtonType.OK) {
                libList.remove(selectedLibrary)
            }
        }
    }

    @FXML
    fun onMoveUp() {
        val lib = selectedLibrary
        if (lib != null) {
            val index = libList.indexOf(lib)
            if (index > 0) {
                libList.removeAt(index)
                libList.add(index - 1, lib)
                listView.selectionModel.select(lib)
            }
        }
    }

    @FXML
    fun onMoveDown() {
        val lib = selectedLibrary
        if (lib != null) {
            val index = libList.indexOf(lib)
            if (index < libList.lastIndex) {
                libList.removeAt(index)
                libList.add(index + 1, lib)
                listView.selectionModel.select(lib)
            }
        }
    }

    @FXML
    fun onSelectLibClick() {
        val file = chooseDirectory(File(selectedLibrary!!.path))
        if (file != null) pathText.text = file.absolutePath
    }

    @FXML
    fun onCancelClick() {
        hide()
    }

    @FXML
    fun onSaveClick() {
        _result = true;
        Config.setLibraries(libList)
        hide()
    }
}