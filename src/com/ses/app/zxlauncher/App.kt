package com.ses.app.zxlauncher

import com.ses.app.zxlauncher.ui.ErrorDialog
import javafx.application.Application
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
    }

    override fun start(primaryStage: Stage?) {
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            //Platform.runLater {
                println(e.stackTraceToString())
                ErrorDialog.create().show(e)
                /*
                Alert(Alert.AlertType.ERROR).apply {
                    title = T("error")
                    headerText = e.message
                    //contentText = e.stackTraceToString()
                }.show()
                */
            //}
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