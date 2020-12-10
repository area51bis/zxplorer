@file:Suppress("FunctionName")

package com.ses.app.zxplorer

import com.ses.zx.SCRImageLoader
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.control.MenuItem
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.image.Image
import javafx.util.Callback
import java.io.File
import kotlin.reflect.KClass

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

//fun Any.fxmlLoader(name: String): FXMLLoader = FXMLLoader(javaClass.getResource(name), App.strings)
fun KClass<*>.fxmlLoader(name: String): FXMLLoader = FXMLLoader(this.java.getResource(name), App.strings)

/** Texto localizado. */
fun T(key: String): String = try {
    App.strings.getString(key)
} catch (e: Exception) {
    key
}

/** Icono. */
fun I(name: String): Image = App::class.java.getResourceAsStream("/icons/$name.png").use { Image(it) }

fun File.toImage(): Image = inputStream().use {
    if (!extension.equals("scr", true)) {
        Image(it)
    } else {
        SCRImageLoader().load(it)
    }
}

fun menuItem(name: String, action: EventHandler<ActionEvent>? = null): MenuItem {
    val menuItem = MenuItem(name)
    if (action != null) menuItem.onAction = action
    return menuItem
}