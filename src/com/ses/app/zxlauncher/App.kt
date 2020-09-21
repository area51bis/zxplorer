package com.ses.app.zxlauncher

import com.ses.app.zxdb.MachineType
import com.ses.app.zxdb.ZXDB
import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage

class App : Application() {
    override fun start(primaryStage: Stage?) {
        val scene = Scene(MainController.load())
        //val controller = loader.getController<MainController>()

        primaryStage?.apply {
            title = "Hello World"
            this.scene = scene
            show()
        }
    }
}

// https://github.com/zxdb/ZXDB/raw/master/ZXDB_mysql.sql
fun main(args: Array<String>) {
    ZXDB.instance.open()
    ZXDB.instance.readTable(MachineType::class)
    ZXDB.instance.close()

    Application.launch(App::class.java, *args)
}
