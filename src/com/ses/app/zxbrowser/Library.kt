package com.ses.app.zxbrowser

import com.ses.app.zxbrowser.model.Model
import com.ses.app.zxbrowser.model.local.LocalModel
import com.ses.app.zxbrowser.model.zxdb.ZXDBModel
import java.io.File

class Library(val type: String, var name: String, var path: String, val source: String? = null, model: Model? = null) : Cloneable {
    companion object {
        const val TYPE_ZXDB = "zxdb"
        const val TYPE_LOCAL = "local"
        const val TYPE_OPENLIB = "openlib"
    }

    val model: Model by lazy {
        model
                ?: when (type) {
                    TYPE_ZXDB -> ZXDBModel(name, File(path))
                    TYPE_LOCAL -> LocalModel(name, File(path))

                    else -> throw Exception("Invalid library '$type'")
                }
    }

    override fun toString(): String = name

    public override fun clone(): Library = Library(type, name, path, source, model)
}
