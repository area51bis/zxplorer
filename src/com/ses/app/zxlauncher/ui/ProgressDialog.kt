package com.ses.app.zxlauncher.ui

import com.ses.app.zxlauncher.fxmlLoader
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.paint.Color
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.net.URL
import java.util.*

class ProgressDialog : Initializable {
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
        initStyle(StageStyle.TRANSPARENT)
        isResizable = false
        initModality(Modality.APPLICATION_MODAL)
    }

    companion object {
        fun create(): ProgressDialog {
            val loader = fxmlLoader("progressdialog.fxml")
            val scene = Scene(loader.load())
            scene.fill = Color.TRANSPARENT // para bordes redondeados
            val dialog: ProgressDialog = loader.getController()
            dialog.stage.scene = scene
            return dialog
        }
    }


    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        title = null
        message = null
    }

    fun show() {
        stage.show()
    }

    fun hide() {
        stage.hide()
    }
}
