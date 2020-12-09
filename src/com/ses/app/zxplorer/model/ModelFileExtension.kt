package com.ses.app.zxplorer.model

import java.io.File
import java.util.zip.ZipFile

class ModelFileExtension(file: File) {
    companion object {
        const val BMP = "bmp"
        const val GIF = "gif"
        const val JPG = "jpg"
        const val PNG = "png"
        const val SCR = "scr"   // Spectrum

        private val IMAGE_EXTENSIONS = arrayOf(BMP, GIF, JPG, PNG, SCR)

        fun isImage(ext: String) : Boolean = IMAGE_EXTENSIONS.contains(ext.toLowerCase())
    }

    /** "AcroJet.tzx.zip" -> "tzx" */
    val rawExtension: String by lazy {
        var n = file.name

        if (isCompressed) {
            n = n.substring(0, n.length-4)
        }

        var ex = n.substringAfterLast(".", "")
        if (ex.isEmpty() && isCompressed) {
            //println(file.name)
            ZipFile(file).use {
                for (entry in it.entries()) {
                    if (!entry.isDirectory) {
                        ex = entry.name.substringAfterLast(".", "")
                        break
                    }
                }
            }
        }

        ex
    }

    /** "AcroJet.tzx.zip" -> ".tzx.zip" */
    val doubleExtension: String by lazy {
        if (ext.toLowerCase() == "zip") {
            val name = file.name
            val i = name.lastIndexOf('.', name.length - 5)
            if (i != -1) {
                val singleExtension = name.substring(i + 1, name.length - 4)
                return@lazy ".$singleExtension.$ext"
            }
        }

        ".$ext"
    }

    /** "AcroJet.tzx.zip" -> ".tzx" */
    //val singleExtension: String

    val ext: String by lazy { file.extension }

    val isCompressed: Boolean by lazy { file.name.toLowerCase().endsWith(".zip") }

    val isImage: Boolean by lazy { IMAGE_EXTENSIONS.contains(rawExtension) }
}
