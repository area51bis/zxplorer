package com.ses.app.zxlauncher

import com.ses.app.zxdb.ZXDB
import com.ses.app.zxdb.dao.Entry
import javafx.beans.property.ReadOnlyStringWrapper
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.util.Callback
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
    lateinit var treeView: TreeView<Category>

    @FXML
    lateinit var tableView: TableView<Entry>

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        createTree()
        createTable()
    }

    private fun createTree() {
        val root = TreeItem(Category("root")).apply {
            isExpanded = true
            children.addAll(
                    TreeItem(Category("Arcade")),
                    TreeItem(Category("Adventure")),
                    TreeItem(Category("Puzzle"))
            )
        }

        treeView.root = root
        treeView.selectionModel.selectedItemProperty().addListener { observable: ObservableValue<out TreeItem<Category>>?, oldValue: TreeItem<Category>?, newValue: TreeItem<Category> ->
            val category = newValue.value
            println("Category: " + category.name)
        }
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
}
