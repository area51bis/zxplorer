package com.ses.app.zxlauncher

import com.ses.app.zxdb.Entry
import com.ses.app.zxdb.ZXDB
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

class Test: Application() {
    override fun start(primaryStage: Stage?) {
        val loader = FXMLLoader(javaClass.getResource("main.fxml"))
        val root = loader.load<Parent>()
        val scene = Scene(root)
        //val controller = loader.getController<MainController>()

        primaryStage?.apply {
            title = "Hello World"
            this.scene = scene
            show()
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            ZXDB.instance.open()
            ZXDB.instance.readTable<Entry>(Entry::class)

            launch(Test::class.java, *args)
        }
    }
}