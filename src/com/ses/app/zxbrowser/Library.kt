package com.ses.app.zxbrowser

import com.ses.app.zxbrowser.model.Model
import com.ses.app.zxbrowser.model.local.LocalModel
import com.ses.app.zxbrowser.model.zxdb.ZXDBModel
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
