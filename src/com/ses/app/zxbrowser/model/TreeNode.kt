package com.ses.app.zxbrowser.model

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.TreeItem

class TreeNode(name: String) : TreeItem<String>(name) {
    val entries: ObservableList<ModelEntry> = FXCollections.observableArrayList()

    fun addEntry(e: ModelEntry) {
        entries.add(e)
    }

    fun addEntries(list: Collection<ModelEntry>) {
        entries.addAll(list)
    }

    fun getNode(path: String): TreeNode {
        return getNode(path.split("|"))
    }

    fun getNode(path: List<String>): TreeNode {
        var node: TreeItem<String> = this

        path.forEach { pathPart ->
            val n = node.children.find { item -> item.value == pathPart }

            if (n != null) {
                node = n
            } else {
                TreeNode(pathPart).also { cat ->
                    node.children.add(cat)
                    node = cat
                }
            }
        }

        return node as TreeNode
    }
}
