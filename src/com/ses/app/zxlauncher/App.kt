package com.ses.app.zxlauncher

import com.ses.zxdb.ZXDB
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
        ZXDB.load()

        val scene = Scene(MainController.load())
        //val controller = loader.getController<MainController>()

        primaryStage?.apply {
            title = "ZX Launcher"
            this.scene = scene
            show()
        }
    }

    override fun stop() {
        super.stop()
        //ZXDB.release()
    }
}
