package com.ses.app.zxbrowser.ui

import com.ses.app.zxbrowser.fxmlLoader
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.paint.Color
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.Window
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
        /*
        initStyle(StageStyle.UTILITY)
        */
        setOnCloseRequest { it.consume() }
        isResizable = false
        initModality(Modality.WINDOW_MODAL)
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

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        title = null
        message = null
    }

    fun show(owner: Window? = null) {
        if (owner != null) stage.initOwner(owner)
        stage.show()
    }

    fun hide() {
        stage.hide()
    }
}