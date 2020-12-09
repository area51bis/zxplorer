package com.ses.app.zxplorer.model

import javafx.scene.control.ContextMenu
import javafx.scene.control.TreeCell

class TreeNodeCell(private val onContextMenu: ((node: TreeNode?, contextMenu: ContextMenu) -> Unit)? = null) : TreeCell<String>() {
    init {
        if (onContextMenu != null) {
            contextMenu = ContextMenu()
            setOnContextMenuRequested { e ->
                onContextMenu.invoke(treeItem as? TreeNode, contextMenu)

                contextMenu.show(this, e.screenX, e.screenY)
            }
        }
    }

    override fun updateItem(item: String?, empty: Boolean) {
        super.updateItem(item, empty)

        if (empty) {
            text = null
            graphic = null
        } else {
            text = item ?: ""
            graphic = treeItem.graphic
        }
    }
}