package com.ses.app.zxplorer.model.zxcollection.editor

import com.ses.app.zxplorer.zxcollection.ReleaseDate
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.collections.FXCollections
import javafx.scene.control.TableColumn
import javafx.scene.control.cell.ComboBoxTableCell
import javafx.scene.control.cell.TextFieldTableCell
import javafx.util.Callback
import javafx.util.StringConverter
import kotlin.reflect.KMutableProperty
import kotlin.reflect.cast
import kotlin.reflect.jvm.jvmErasure

val ReleaseDate.Companion.stringConverter: StringConverter<ReleaseDate> by lazy {
    object : StringConverter<ReleaseDate>() {
        override fun toString(date: ReleaseDate?): String = date.toString()
        override fun fromString(s: String?): ReleaseDate = ReleaseDate.from(s)
    }
}

class StringColumnEditor<T>(private val column: TableColumn<T, *>, private val propertyName: String, private val converter: StringConverter<*>? = null) {
    private var _property: KMutableProperty<*>? = null

    fun configure() {
        _property = null

        column.cellValueFactory = Callback { o ->
            val prop = getProperty(o.value)
            ReadOnlyObjectWrapper(prop.getter.call(o.value))
        }

        if (converter != null) {
            column.cellFactory = TextFieldTableCell.forTableColumn(converter)
        } else {
            column.cellFactory = TextFieldTableCell.forTableColumn()
        }

        column.setOnEditCommit { ev ->
            val prop = getProperty(ev.rowValue)
            prop.setter.call(ev.rowValue, prop.returnType.jvmErasure.cast(ev.newValue))
        }
    }

    private fun getProperty(o: T): KMutableProperty<*> {
        if (_property == null) {
            _property = o!!::class.members.find { it.name == propertyName } as KMutableProperty<*>
        }

        return _property!!
    }
}

class ComboColumnEditor<T>(private val column: TableColumn<T, *>, private val propertyName: String, val list: Collection<*>) {
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
        if (_property == null) {
            _property = o!!::class.members.find { it.name == propertyName } as KMutableProperty<*>
        }

        return _property!!
    }
}
