package com.ses.app.zxplorer.model.zxcollection

import com.ses.app.zxplorer.ui.AppDialog
import com.ses.app.zxplorer.zxcollection.*
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.ReadOnlyStringWrapper
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.control.TableView
import javafx.scene.control.cell.ComboBoxTableCell
import javafx.scene.control.cell.TextFieldTableCell
import javafx.util.Callback
import javafx.util.StringConverter
import java.net.URL
import java.util.*


class ZXCollectionEditor : AppDialog<Unit>() {
    private var zxc: ZXCollection? = null
        set(value) {
            field = value
            if (value != null) {
                entriesTable.items.setAll(value.entries)
                stage.title = value.info.name
            } else {
                entriesTable.items.clear()
                stage.title = "<new>"
            }
        }

    @FXML
    lateinit var entriesTable: TableView<Entry>

    @FXML
    lateinit var downloadsTable: TableView<Download>

    companion object {
        fun create(zxc: ZXCollection? = null) = create(ZXCollectionEditor::class, "zxcollection_editor.fxml").also {
            it.zxc = zxc ?: ZXCollection()
        }
    }

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        super.initialize(location, resources)

        initEntriesTable()
        initDownloadsTable()
    }

    private fun initEntriesTable() {
        with(entriesTable.columns) {
            this[0].cellValueFactory = Callback { SimpleStringProperty(it.value.title) }
            this[0].cellFactory = TextFieldTableCell.forTableColumn()
            this[0].setOnEditCommit { it.rowValue.title = it.newValue as String }

            this[1].cellValueFactory = Callback { ReadOnlyObjectWrapper(it.value.genre?.text) }
            this[1].cellFactory = ComboBoxTableCell.forTableColumn(FXCollections.observableArrayList(ZXCollection.genres()))
            this[1].setOnEditCommit { it.rowValue.genre = it.newValue as Genre }

            this[2].cellValueFactory = Callback { ReadOnlyObjectWrapper(it.value.releaseDate) }
            this[2].cellFactory = TextFieldTableCell.forTableColumn(object : StringConverter<ReleaseDate>() {
                override fun toString(date: ReleaseDate?): String = date.toString()
                override fun fromString(s: String?): ReleaseDate = ReleaseDate.from(s)
            })
            this[2].setOnEditCommit { it.rowValue.releaseDate = it.newValue as ReleaseDate }

            this[3].cellValueFactory = Callback { ReadOnlyStringWrapper(it.value.machines?.first()?.text) }
            this[4].cellValueFactory = Callback { ReadOnlyStringWrapper(it.value.availability?.text) }
        }

        entriesTable.selectionModel.selectedItemProperty().addListener { _, _, entry ->
            if (entry != null) {
                downloadsTable.items.setAll(entry.downloads)
            } else {
                downloadsTable.items.clear()
            }
        }
    }

    private fun initDownloadsTable() {
        with(downloadsTable.columns) {
            this[0].cellValueFactory = Callback { ReadOnlyStringWrapper(it.value.fileName) }
            this[1].cellValueFactory = Callback { ReadOnlyStringWrapper(it.value.type?.text) }
            this[2].cellValueFactory = Callback { ReadOnlyObjectWrapper(it.value.releaseDate) }
            this[3].cellValueFactory = Callback { ReadOnlyStringWrapper(it.value.machine?.text) }
        }
    }
}