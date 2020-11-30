package com.ses.app.zxbrowser.ui

import com.ses.app.zxbrowser.*
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.input.MouseEvent
import javafx.scene.layout.GridPane
import javafx.stage.DirectoryChooser
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
        fun create() = create<EditLibsDialog>("edit_libraries_dialog.fxml")
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

        updateProgramInfo(null)
    }

    override fun getResult(): Boolean? = _result

    private fun selectLibrary(lib: Library) {
        selectedLibrary = null
        updateProgramInfo(lib)
        selectedLibrary = lib
    }

    private fun updateProgramInfo(program: Library?) {
        editView.isDisable = (program == null)
        if (program != null) {
            typeText.text = program.type
            nameText.text = program.name
            pathText.text = program.path
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

    private val addContextMenu = ContextMenu().apply {
        items.addAll(
                MenuItem(T("lib_type_zxdb")).apply { setOnAction { addLibrary(Library.TYPE_ZXDB) } },
                MenuItem(T("lib_type_local")).apply { setOnAction { addLibrary(Library.TYPE_LOCAL) } },
        )
    }

    private fun addLibrary(type: String) {
        var lib: Library? = null

        when (type) {
            Library.TYPE_ZXDB -> lib = Library(type, "ZXDB", "zxdb", )
            Library.TYPE_LOCAL -> {
                val dir = chooseDirectory()
                if (dir != null) {
                    lib = Library(type, dir.name, dir.absolutePath, )
                }
            }
        }

        if (lib != null) {
            libList.add(lib)
            listView.selectionModel.select(lib)
        }
    }

    @FXML
    fun onAddClick(e: MouseEvent) {
        addContextMenu.show(e.source as Button, e.screenX, e.screenY)
        /*
        val file = chooseDirectory()
        if (file != null) {
            val lib: Library = Library("zxdb", "ZXDB", "zxdb")
            libList.add(lib)
            listView.selectionModel.select(lib)
        }
        */
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
                //selectedProgram = null
            }
        }
    }

    @FXML
    fun onMoveUp() {
        val program = selectedLibrary
        if (program != null) {
            val index = libList.indexOf(program)
            if (index > 0) {
                libList.removeAt(index)
                libList.add(index - 1, program)
                listView.selectionModel.select(program)
            }
        }
    }

    @FXML
    fun onMoveDown() {
        val program = selectedLibrary
        if (program != null) {
            val index = libList.indexOf(program)
            if (index < libList.lastIndex) {
                libList.removeAt(index)
                libList.add(index + 1, program)
                listView.selectionModel.select(program)
            }
        }
    }

    @FXML
    fun onSelectProgramClick() {
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