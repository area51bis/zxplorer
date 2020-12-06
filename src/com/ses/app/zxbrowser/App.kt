package com.ses.app.zxbrowser

import com.ses.app.zxbrowser.ui.ErrorDialog
import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Scene
import javafx.stage.Stage
import java.io.File
import java.util.*

// java --module-path C:\dev\java\jdk\javafx15\lib --add-modules javafx.controls,javafx.graphics,javafx.fxml -jar ZXBrowser.jar
class App : Application() {
    companion object {
        val strings = ResourceBundle.getBundle("strings")!!
        val workingDir = File(System.getProperty("user.dir"))

        lateinit var mainStage: Stage
        lateinit var mainScene: Scene

        fun localFile(localPath: String): File = File(workingDir, localPath)
    }

    override fun start(primaryStage: Stage?) {
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            Platform.runLater {
                println(e.stackTraceToString())
                ErrorDialog().show(e)
            }
        }

        mainStage = primaryStage!!

        val scene = Scene(MainController.load())
        mainScene = scene

        primaryStage.apply {
            title = FULL_APP_NAME
            this.scene = scene
            show()
        }
    }
}
