package com.ses.app.zxlauncher.ui

import com.ses.app.zxlauncher.T
import com.ses.app.zxlauncher.fxmlLoader
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.net.URL
import java.util.*

class ErrorDialog : Initializable {
    @FXML
    lateinit var messageLabel: Label

    @FXML
    lateinit var detailsText: TextArea

    var title: String?
        get() = stage.title
        set(value) {
            stage.title = value
        }

    var message: String?
        get() = messageLabel.text
        set(value) {
            messageLabel.text = value
        }

    var details: String?
        get() = detailsText.text
        set(value) {
            detailsText.text = value
        }

    private val stage: Stage = Stage().apply {
        initStyle(StageStyle.UTILITY)
        isResizable = true
        initModality(Modality.APPLICATION_MODAL)
    }

    companion object {
        fun create(): ErrorDialog {
            val loader = fxmlLoader("errordialog.fxml")
            val scene = Scene(loader.load())
            val dialog: ErrorDialog = loader.getController()
            dialog.stage.scene = scene
            return dialog
        }
    }

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        title = null
        message = null
        details = null
    }

    fun show(e: Throwable) {
        stage.title = T("error")
        message = e.localizedMessage
        details = e.stackTraceToString()
        show()
    }

    fun show() {
        stage.show()
    }

    fun hide() {
        stage.hide()
    }

    @FXML
    fun onOkButtonClick() {
        hide()
    }
}
