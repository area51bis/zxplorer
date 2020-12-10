package com.ses.app.zxplorer.ui

import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.net.URL
import java.util.*

class ProgressDialog : AppDialog<Unit>() {
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

    companion object {
        fun create() = create(ProgressDialog::class, "progressdialog.fxml").apply {
            stage.scene.fill = Color.TRANSPARENT // para bordes redondeados
        }
    }

    override fun createStage(): Stage = super.createStage().apply {
        initStyle(StageStyle.TRANSPARENT)
        setOnCloseRequest { it.consume() }
        isResizable = false
    }

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        super.initialize(location, resources)

        title = null
        message = null
    }
}
