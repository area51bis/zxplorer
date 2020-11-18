package com.ses.app.zxbrowser.ui

import com.ses.app.zxbrowser.fxmlLoader
import javafx.fxml.Initializable
import javafx.scene.Scene
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.Window
import java.net.URL
import java.util.*

abstract class AppDialog : Initializable {
    protected val stage: Stage by lazy { createStage() }

    companion object {
        fun <T : AppDialog> create(fxmlName: String): T {
            val loader = fxmlLoader(fxmlName)
            val scene = Scene(loader.load())
            val dialog: T = loader.getController()
            dialog.stage.scene = scene
            return dialog
        }
    }

    open fun createStage(): Stage = Stage().apply {
        initStyle(StageStyle.DECORATED)
        initModality(Modality.WINDOW_MODAL)
    }

    override fun initialize(location: URL?, resources: ResourceBundle?) {}

    fun show(owner: Window? = null) {
        if (owner != null) stage.initOwner(owner)
        stage.show()
    }

    fun showAndWait(owner: Window? = null) {
        if (owner != null) stage.initOwner(owner)
        stage.showAndWait()
    }

    fun hide() {
        stage.hide()
    }
}
