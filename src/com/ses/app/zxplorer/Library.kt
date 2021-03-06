package com.ses.app.zxplorer

import com.ses.app.zxplorer.model.Model
import com.ses.app.zxplorer.model.local.LocalModel
import com.ses.app.zxplorer.model.zxcollection.ZXCModel
import com.ses.app.zxplorer.model.zxdb.ZXDBModel
import java.io.File

/**
 * @param type Tipo de biblioteca
 * @param path Directorio de los datos/descargas
 * @param source Dirección del origen. Depende de la biblioteca (fichero, directorio, url...)
 */
class Library(val type: String, var name: String, var path: String, val source: String? = null, model: Model? = null) : Cloneable {
    companion object {
        const val TYPE_ZXDB = "zxdb"
        const val TYPE_LOCAL = "local"
        const val TYPE_ZXC = "zxc"
    }

    val model: Model by lazy {
        model ?: when (type) {
            TYPE_ZXDB -> ZXDBModel(name, File(path))
            TYPE_LOCAL -> LocalModel(name, File(path))
            TYPE_ZXC -> ZXCModel(name, File(path), source)

            else -> throw Exception("Invalid library '$type'")
        }
    }

    override fun toString(): String = name

    public override fun clone(): Library = Library(type, name, path, source, model)
}
