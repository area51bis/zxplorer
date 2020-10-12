package com.ses.app.zxlauncher

import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage
import java.io.File

// https://github.com/zxdb/ZXDB/raw/master/ZXDB_mysql.sql

// ZXLauncher
// ZXDatabase
// ZXBrowser
// SpeccyDB
// ZXDBrowser
//

class App : Application() {
    companion object {
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
