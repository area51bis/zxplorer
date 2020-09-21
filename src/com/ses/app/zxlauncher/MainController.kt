package com.ses.app.zxlauncher

import javafx.beans.value.ObservableValue
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
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

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        createTree()
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
}