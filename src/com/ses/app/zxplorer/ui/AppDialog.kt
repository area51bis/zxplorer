package com.ses.app.zxplorer.ui

import com.ses.app.zxplorer.fxmlLoader
import javafx.fxml.Initializable
import javafx.scene.Scene
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.Window
import java.net.URL
import java.util.*
import kotlin.reflect.KClass

abstract class AppDialog<R> : Initializable {
    protected val stage: Stage by lazy { createStage() }

    companion object {
        fun <T : AppDialog<*>> create(cls: KClass<T>, fxmlName: String): T {
            val loader = cls.fxmlLoader(fxmlName)
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

    open fun getResult(): R? = null

    fun show(owner: Window? = null) {
        if (owner != null) stage.initOwner(owner)
        stage.show()
    }

    fun showAndWait(owner: Window? = null): R? {
        if (owner != null) stage.initOwner(owner)
        stage.showAndWait()
        return getResult()
    }

    fun hide() {
        stage.hide()
    }
}
