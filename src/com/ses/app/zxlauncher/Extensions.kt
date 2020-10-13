package com.ses.app.zxlauncher

import javafx.beans.value.ObservableValue
import javafx.fxml.FXMLLoader
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.image.Image
import javafx.util.Callback
import java.io.File
import kotlin.reflect.KClass

/**
Atajo para añadir columnas. Equivalente a:
<pre>
tableView.columns.add(TableColumn<Download, String>("Name").apply {
cellValueFactory = Callback { p -> ReadOnlyStringWrapper(p.value.fileName) }
})
</pre>
 */
fun <S, T> TableView<S>.addColumn(name: String, callback: Callback<TableColumn.CellDataFeatures<S, T>, ObservableValue<T>>): TableColumn<S, T> {
    return TableColumn<S, T>(name).also {
        it.cellValueFactory = callback
        columns.add(it)
    }
}

fun Any.fxmlLoader(name: String): FXMLLoader = FXMLLoader(javaClass.getResource(name), App.strings)
fun Any.T(key: String) = App.strings.getString(key)

fun File.toImage(): Image = inputStream().use { Image(it) }
