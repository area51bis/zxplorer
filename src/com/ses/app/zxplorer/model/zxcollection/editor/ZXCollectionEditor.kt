package com.ses.app.zxplorer.model.zxcollection.editor

import com.ses.app.zxplorer.ui.AppDialog
import com.ses.app.zxplorer.zxcollection.*
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.ReadOnlyStringWrapper
import javafx.beans.property.SimpleStringProperty
import javafx.fxml.FXML
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.TextFieldTableCell
import javafx.util.Callback
import javafx.util.StringConverter
import java.net.URL
import java.util.*
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.cast
import kotlin.reflect.jvm.jvmErasure


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

    //private fun <S, T> setupStringTableColumn( column: TableColumn<S, *>, cls: KClass<*> ) {
    private fun <S> setupStringTableColumn(column: TableColumn<S, *>, propertyName: String) {
        column.cellValueFactory = Callback { o ->
            val prop: KProperty<*> = o.value!!::class.members.find { p -> p.name == propertyName } as KProperty<*>
            SimpleStringProperty(prop.getter.call(o.value).toString())
        }

        column.cellFactory = TextFieldTableCell.forTableColumn()

        column.setOnEditCommit { ev ->
            val prop: KMutableProperty<*> = ev.rowValue!!::class.members.find { p -> p.name == propertyName } as KMutableProperty<*>
            prop.setter.call(ev.rowValue, prop.returnType.jvmErasure.cast(ev.newValue))
        }
    }

    private fun initEntriesTable() {
        with(entriesTable.columns) {
            StringColumnEditor(this[0], "title").configure()
            ComboColumnEditor(this[1], "genre", ZXCollection.genres()).configure()
            StringColumnEditor(this[2], "releaseDate", ReleaseDate.stringConverter).configure()

            this[3].cellValueFactory = Callback { ReadOnlyStringWrapper(it.value.machines?.first()?.text) }
            ComboColumnEditor(this[4], "availability", ZXCollection.availabilityTypes()).configure()
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
