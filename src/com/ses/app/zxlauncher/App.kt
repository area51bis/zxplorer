package com.ses.app.zxlauncher

import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage
import java.io.File
import java.util.*

class App : Application() {
    companion object {
        val strings = ResourceBundle.getBundle("strings")!!
        val workingDir = File(System.getProperty("user.dir"))

        lateinit var mainStage: Stage
        lateinit var mainScene: Scene
    }

    override fun start(primaryStage: Stage?) {
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
