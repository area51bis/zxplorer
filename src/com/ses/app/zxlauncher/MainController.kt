package com.ses.app.zxlauncher

import com.ses.net.Http
import com.ses.zxdb.ZXDB
import com.ses.zxdb.dao.Entry
import com.ses.zxdb.dao.GenreType
import javafx.beans.property.ReadOnlyStringWrapper
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TreeView
import javafx.util.Callback
import java.io.File
import java.net.URL
import java.util.*


class MainController : Initializable {
    companion object {
        fun load(): Parent {
            val loader = FXMLLoader(MainController::class.java.getResource("main.fxml"))
            return loader.load()
        }
    }

    @FXML
    lateinit var treeView: TreeView<String>

    @FXML
    lateinit var tableView: TableView<Entry>

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        createTree()
        createTable()
    }

    private fun createTree() {
        treeView.root = TreeGenreItem("ZXDB")

        // crear los nodos en el orden de las categorías
        ZXDB.instance.getTable(GenreType::class).rows.forEach { genre ->
            getCategoryNode(genre.text!!)
        }

        // añadir las entradas a los nodos
        ZXDB.instance.getTable(Entry::class).rows.forEach { entry ->
            addTreeEntry(entry)
        }

        treeView.selectionModel.selectedItemProperty().addListener { observable, oldValue, newValue ->
            val category = newValue as TreeGenreItem
            //println("Category: ${category.value}")
            tableView.items = category.entries
        }
    }

    /** Añade una entrada a los nodos correspondientes, creando los necesarios. */
    private fun addTreeEntry(entry: Entry) {
        val path = ZXDBUtil.getCategoryPath(entry)

        var node = treeView.root as TreeGenreItem
        node.addEntry(entry)

        path.forEach { pathPart ->
            val n = node.children.find { item -> item.value == pathPart }

            if (n != null) {
                node = n as TreeGenreItem
            } else {
                TreeGenreItem(pathPart).also { cat ->
                    node.children.add(cat)
                    //node.children.sortBy { item -> item.value }
                    node = cat
                }
            }

            node.addEntry(entry)
        }
    }

    /** Obtiene un nodo de una categoría, creando los necesarios. */
    private fun getCategoryNode(name: String): TreeGenreItem {
        val path: List<String> = ZXDBUtil.getCategoryPath(name)
        var node = treeView.root

        path.forEach { pathPart ->
            val n = node.children.find { item -> item.value == pathPart }

            if (n != null) {
                node = n
            } else {
                TreeGenreItem(pathPart).also { cat ->
                    node.children.add(cat)
                    //node.children.sortBy { item -> item.value }
                    node = cat
                }
            }
        }

        return node as TreeGenreItem
    }

    private fun createTable() {
        tableView.columns.add(TableColumn<Entry, String>("Title").apply {
            cellValueFactory = Callback { p -> ReadOnlyStringWrapper(p.value.title) }
        })

        tableView.columns.add(TableColumn<Entry, String>("Category").apply {
            cellValueFactory = Callback { p -> ReadOnlyStringWrapper(ZXDB.instance.getGenre(p.value.genretype_id)?.text) }
        })

        val listData: ObservableList<Entry> = FXCollections.observableArrayList()
        ZXDB.instance.getTable(Entry::class).rows.forEach { listData.add(it) }
        tableView.items = listData
    }

    @FXML
    fun menuUpdateDatabaseAction() {
        val workingDir = File(System.getProperty("user.dir"))
        val file = File(workingDir, "ZXDB_mysql.sql")

        // descargar ZXDB_mysql.sql
        Http().apply {
            request = "https://github.com/zxdb/ZXDB/raw/master/ZXDB_mysql.sql"
            getFile(file) { status, progress ->
            }
        }

        // convertir a sqlite

        // sustituir fichero

        // recargar base de datos y refrescar los datos
    }

    @FXML
    fun menuAboutAction() {
        Alert(Alert.AlertType.INFORMATION).apply {
            title = "About"
            //headerText = null
            contentText = "ZXLauncher 0.001"
        }.showAndWait()
    }
}
