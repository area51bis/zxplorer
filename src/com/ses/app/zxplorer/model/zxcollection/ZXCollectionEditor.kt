package com.ses.app.zxplorer.model.zxcollection

import com.ses.app.zxplorer.ui.AppDialog
import com.ses.app.zxplorer.zxcollection.Download
import com.ses.app.zxplorer.zxcollection.Entry
import com.ses.app.zxplorer.zxcollection.ZXCollection
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.ReadOnlyStringWrapper
import javafx.beans.property.SimpleStringProperty
import javafx.fxml.FXML
import javafx.scene.control.TableView
import javafx.scene.control.cell.TextFieldTableCell
import javafx.util.Callback
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
            this[0].setOnEditCommit { event ->
                event.rowValue.title = event.newValue as String
            }
            this[1].cellValueFactory = Callback { ReadOnlyStringWrapper(it.value.genre?.text) }
            this[2].cellValueFactory = Callback { ReadOnlyObjectWrapper(it.value.releaseDate) }
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