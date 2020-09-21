package com.ses.app.zxlauncher

import com.ses.app.zxdb.MachineType
import com.ses.app.zxdb.ZXDB
import javafx.application.Application
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.TreeView
import javafx.stage.Stage

// https://github.com/zxdb/ZXDB/raw/master/ZXDB_mysql.sql
class Main: Application() {
    @FXML
    private val treeView: TreeView<Category>? = null

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
            ZXDB.instance.readTable(MachineType::class)
            ZXDB.instance.close()

            launch(Main::class.java, *args)
        }
    }
}