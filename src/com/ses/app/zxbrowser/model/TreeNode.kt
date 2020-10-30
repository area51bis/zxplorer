package com.ses.app.zxbrowser.model

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.TreeItem
import javafx.scene.image.Image
import javafx.scene.image.ImageView

class TreeNode(name: String) : TreeItem<String>(name) {
    val entries: ObservableList<ModelEntry> = FXCollections.observableArrayList()

    var expandedIcon: Image? = null
        set(value) { field = value; updateIcon() }
    var collapsedIcon: Image? = null
        set(value) { field = value; updateIcon() }

    private val imageView = ImageView()

    init {
        graphic = imageView
        expandedProperty().addListener { _, _, _ -> updateIcon() }
    }

    fun addEntry(e: ModelEntry) {
        entries.add(e)
    }

    fun addEntries(list: Collection<ModelEntry>) {
        entries.addAll(list)
    }

    fun getNode(path: String, callback: ((newNode: TreeNode) -> Unit)? = null): TreeNode {
        return getNode(path.split("|"), callback)
    }

    fun getNode(path: List<String>, callback: ((newNode: TreeNode) -> Unit)? = null): TreeNode {
        var node: TreeItem<String> = this

        path.forEach { pathPart ->
            val n = node.children.find { item -> item.value == pathPart }

            if (n != null) {
                node = n
            } else {
                TreeNode(pathPart).also { cat ->
                    node.children.add(cat)
                    node = cat
                    if (callback != null) callback(cat)
                }
            }
        }

        return node as TreeNode
    }

    private fun updateIcon() {
        imageView.image = if (isExpanded) {
            expandedIcon ?: collapsedIcon
        } else {
            collapsedIcon ?: expandedIcon
        }
    }
}
