package com.ses.app.zxlauncher.ui

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle

class ProgressDialog {
    @FXML
    lateinit var titleLabel: Label

    @FXML
    lateinit var messageLabel: Label

    @FXML
    lateinit var progressBar: ProgressBar

    var title: String?
        get() = titleLabel.text
        set(value) {
            titleLabel.text = value
        }

    var message: String?
        get() = messageLabel.text
        set(value) {
            messageLabel.text = value
        }

    var progress: Double
        get() = progressBar.progress
        set(value) {
            progressBar.progress = value
        }

    private val stage: Stage = Stage().apply {
        initStyle(StageStyle.UNDECORATED)
        isResizable = false
        initModality(Modality.APPLICATION_MODAL)
    }

    companion object {
        fun create(): ProgressDialog {
            val loader = FXMLLoader(ProgressDialog::class.java.getResource("progressdialog.fxml"))
            val scene = Scene(loader.load())
            val dialog: ProgressDialog = loader.getController()
            dialog.stage.scene = scene
            return dialog
        }
    }

    fun show() {
        stage.show()
    }

    fun hide() {
        stage.hide()
    }
}
