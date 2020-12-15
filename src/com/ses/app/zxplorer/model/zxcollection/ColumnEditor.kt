package com.ses.app.zxplorer.model.zxcollection

import com.ses.app.zxplorer.zxcollection.ZXCollection
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.scene.control.cell.ComboBoxTableCell
import javafx.scene.control.cell.TextFieldTableCell
import javafx.util.Callback
import kotlin.reflect.KMutableProperty
import kotlin.reflect.cast
import kotlin.reflect.jvm.jvmErasure

class StringColumnEditor<T>(private val column: TableColumn<T, *>, private val propertyName: String ) {
    private var _property: KMutableProperty<*>? = null

    fun configure() {
        _property = null

        column.cellValueFactory = Callback { o ->
            val prop = getProperty(o.value)
            ReadOnlyObjectWrapper(prop.getter.call(o.value))
        }

        column.cellFactory = TextFieldTableCell.forTableColumn()

        column.setOnEditCommit { ev ->
            val prop = getProperty(ev.rowValue)
            prop.setter.call(ev.rowValue, prop.returnType.jvmErasure.cast(ev.newValue))
        }
    }

    private fun getProperty(o: T): KMutableProperty<*> {
        if (_property == null ) {
            _property = o!!::class.members.find { it.name == propertyName } as KMutableProperty<*>
        }

        return _property!!
    }
}

class ComboColumnEditor<T>(private val column: TableColumn<T, *>, private val propertyName: String, val list: Collection<*> ) {
    private var _property: KMutableProperty<*>? = null

    fun configure() {
        _property = null

        column.cellValueFactory = Callback { o ->
            val prop = getProperty(o.value)
            ReadOnlyObjectWrapper(prop.getter.call(o.value))
        }

        column.cellFactory = ComboBoxTableCell.forTableColumn(FXCollections.observableArrayList(list))

        column.setOnEditCommit { ev ->
            val prop = getProperty(ev.rowValue)
            prop.setter.call(ev.rowValue, prop.returnType.jvmErasure.cast(ev.newValue))
        }
    }

    private fun getProperty(o: T): KMutableProperty<*> {
        if (_property == null ) {
            _property = o!!::class.members.find { it.name == propertyName } as KMutableProperty<*>
        }

        return _property!!
    }
}
