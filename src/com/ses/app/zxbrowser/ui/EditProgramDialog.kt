package com.ses.app.zxbrowser.ui

import com.ses.app.zxbrowser.Config
import com.ses.app.zxbrowser.Program
import com.ses.app.zxbrowser.T
import com.ses.app.zxbrowser.fxmlLoader
import javafx.fxml.FXML
import javafx.scene.Scene
import javafx.scene.control.CheckBox
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import javafx.scene.paint.Color
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.net.URL
import java.util.*

class EditProgramDialog : AppDialog() {
    @FXML
    lateinit var listView: ListView<Program>

    lateinit var editView: GridPane

    @FXML
    lateinit var idText: TextField

    @FXML
    lateinit var nameText: TextField

    @FXML
    lateinit var pathText: TextField

    @FXML
    lateinit var argsText: TextField

    @FXML
    lateinit var extText: TextField

    @FXML
    lateinit var unzipCheck: CheckBox

    private var selectedProgram: Program? = null

    companion object {
        fun create() = create<EditProgramDialog>("edit_program_dialog.fxml")
    }

    override fun createStage(): Stage = super.createStage().apply {
        initStyle(StageStyle.UTILITY)
        isResizable = false
    }

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        super.initialize(location, resources)
        stage.title = T("configure_programs")

        Config.allPrograms.forEach { listView.items.add(it.clone()) }
        listView.selectionModel.selectedItemProperty().addListener { _, _, program ->
            selectProgram(program)
        }

        idText.textProperty().addListener { _, _, text -> selectedProgram?.id = text }
        nameText.textProperty().addListener { _, _, text -> selectedProgram?.name = text; listView.refresh() }
        pathText.textProperty().addListener { _, _, text -> selectedProgram?.path = text }
        argsText.textProperty().addListener { _, _, text -> selectedProgram?.args = text }
        extText.textProperty().addListener { _, _, text -> selectedProgram?.ext = text.split(",").map { it.trim() }.toTypedArray() }
        unzipCheck.selectedProperty().addListener { _, _, selected -> selectedProgram?.unzip = selected }

        updateProgramInfo(null)
    }

    private fun selectProgram(program: Program?) {
        selectedProgram = null
        updateProgramInfo(program)
        selectedProgram = program
    }

    private fun updateProgramInfo(program: Program?) {
        editView.isDisable = (program == null)
        if (program != null) {
            idText.text = program.id
            nameText.text = program.name
            pathText.text = program.path
            argsText.text = program.args
            extText.text = program.ext.joinToString(",")
            unzipCheck.isSelected = program.unzip
        } else {
            idText.text = null
            nameText.text = null
            pathText.text = null
            argsText.text = null
            extText.text = null
            unzipCheck.isSelected = false
        }
    }

    @FXML
    fun onCancelClick() {
        hide()
    }

    fun onOkClick() {
        Config.setPrograms(listView.items)
        hide()
    }
}