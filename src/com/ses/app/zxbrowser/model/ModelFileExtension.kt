package com.ses.app.zxbrowser.model

import java.io.File
import java.util.zip.ZipFile

class ModelFileExtension {
    companion object {
        const val BMP = "bmp"
        const val GIF = "gif"
        const val JPG = "jpg"
        const val PNG = "png"
        const val SCR = "scr"   // Spectrum

        private val IMAGE_EXTENSIONS = arrayOf(BMP, GIF, JPG, PNG, SCR)
    }

    /** "AcroJet.tzx.zip" -> "tzx" */
    val rawExtension: String

    /** "AcroJet.tzx.zip" -> ".tzx.zip" */
    val doubleExtension: String

    /** "AcroJet.tzx.zip" -> ".tzx" */
    val singleExtension: String

    val isCompressed: Boolean

    val isImage: Boolean

    constructor(file: File) {
        var n = file.name
        var zipExt = "zip"
        var zipSuffix = ".zip"
        isCompressed = n.endsWith(zipSuffix, true)
        if (isCompressed) {
            zipExt = n.substring(n.length - 3)
            zipSuffix = n.substring(n.length - 4)
            n = n.removeSuffix(zipSuffix)
        }
        var ext = n.substringAfterLast(".", "")
        if (ext.isEmpty() && isCompressed) {
            ZipFile(file).use {
                for (entry in it.entries()) {
                    if (!entry.isDirectory) {
                        ext = entry.name.substringAfterLast(".", "")
                        break
                    }
                }
            }

            rawExtension = ext
            singleExtension = ".$rawExtension"
            doubleExtension = zipSuffix

        } else {
            rawExtension = ext
            singleExtension = if (rawExtension.isEmpty()) "" else ".$rawExtension"
            doubleExtension = if (isCompressed && (rawExtension != zipExt)) "$singleExtension$zipSuffix" else singleExtension
        }

        isImage = IMAGE_EXTENSIONS.contains(rawExtension)
    }
}
