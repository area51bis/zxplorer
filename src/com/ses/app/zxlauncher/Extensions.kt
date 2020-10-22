package com.ses.app.zxlauncher

import javafx.beans.value.ObservableValue
import javafx.fxml.FXMLLoader
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.image.Image
import javafx.util.Callback
import java.io.File

/**
Atajo para añadir columnas. Equivalente a:
```
tableView.columns.add(TableColumn<Download, String>("Name").apply {
cellValueFactory = Callback { p -> ReadOnlyStringWrapper(p.value.fileName) }
})
```
 */
fun <S, T> TableView<S>.addColumn(name: String, callback: Callback<TableColumn.CellDataFeatures<S, T>, ObservableValue<T>>): TableColumn<S, T> {
    return TableColumn<S, T>(name).also {
        it.cellValueFactory = callback
        columns.add(it)
    }
}

fun Any.fxmlLoader(name: String): FXMLLoader = FXMLLoader(javaClass.getResource(name), App.strings)

fun Any.T(key: String): String = try {
    App.strings.getString(key)
} catch (e: Exception) {
    key
}

val File.doubleExtension: String
    get() {
        val _name = name.toLowerCase()

        var end: Int
        var ext = ""
        if (_name.endsWith(".zip") ) {
            end = _name.lastIndex - 4
            ext = ".zip"
        } else {
            end = _name.lastIndex
        }
        val i = _name.lastIndexOf('.', end)
        return if( i != -1) _name.substring(i) else ext
    }

fun File.toImage(): Image = inputStream().use { Image(it) }
