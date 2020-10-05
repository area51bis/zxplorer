package com.ses.app.zxlauncher

import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage
import java.io.File

// https://github.com/zxdb/ZXDB/raw/master/ZXDB_mysql.sql

class App : Application() {
    companion object {
        val workingDir = File(System.getProperty("user.dir"))
    }

    override fun start(primaryStage: Stage?) {
        val scene = Scene(MainController.load())

        primaryStage?.apply {
            title = "ZX Launcher"
            this.scene = scene
            show()
        }
    }
}
