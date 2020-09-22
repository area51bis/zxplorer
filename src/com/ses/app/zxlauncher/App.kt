package com.ses.app.zxlauncher

import com.ses.app.zxdb.ZXDB
import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage

// https://github.com/zxdb/ZXDB/raw/master/ZXDB_mysql.sql

class App : Application() {
    override fun start(primaryStage: Stage?) {
        ZXDB.instance.load()

        val scene = Scene(MainController.load())
        //val controller = loader.getController<MainController>()

        primaryStage?.apply {
            title = "ZX Launcher"
            this.scene = scene
            show()
        }
    }
}
