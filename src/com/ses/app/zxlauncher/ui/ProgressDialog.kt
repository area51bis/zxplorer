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
    lateinit var messageLabel: Label

    @FXML
    lateinit var progressBar: ProgressBar

    private val stage: Stage = Stage().apply {
        initStyle(StageStyle.UTILITY)
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
}
