package com.ses.app.zxbrowser.ui

import com.ses.app.zxbrowser.T
import javafx.scene.control.Alert
import javafx.scene.control.TextArea
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority

// https://code.makery.ch/blog/javafx-dialogs-official/
class ErrorDialog {
    var title: String? = null
    var message: String? = null
    var details: String? = null

    fun show(e: Throwable) {
        title = T("error")
        message = e.cause?.localizedMessage ?: e.localizedMessage
        details = e.stackTraceToString()
        show()
    }

    fun show() {
        val alert = Alert(Alert.AlertType.ERROR).also {
            it.title = title
            it.headerText = null
            it.contentText = message
        }

        // Create expandable Exception.
        val textArea = TextArea(details).apply {
            isEditable = false
            isWrapText = true
            maxWidth = Double.MAX_VALUE
            maxHeight = Double.MAX_VALUE
            GridPane.setVgrow(this, Priority.ALWAYS)
            GridPane.setHgrow(this, Priority.ALWAYS)
        }

        val expContent = GridPane().apply {
            maxWidth = Double.MAX_VALUE
            add(textArea, 0, 1)
        }

        // Set expandable Exception into the dialog pane.
        alert.dialogPane.expandableContent = expContent

        alert.showAndWait()
    }
}
