package com.ses.app.zxlauncher

import com.ses.app.zxlauncher.model.Model
import com.ses.app.zxlauncher.model.local.LocalModel
import com.ses.app.zxlauncher.model.zxdb.ZXDBModel
import java.io.File

class Library(val type: String, val name: String, val path: String) {
    val model: Model by lazy {
        when (type) {
            "zxdb" -> ZXDBModel(name, File(path))
            "local" -> LocalModel(name, File(path))

            else -> throw Exception("Invalid library '$type'")
        }
    }
}
