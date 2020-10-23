package com.ses.app.zxlauncher.model

import java.io.File

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

    constructor(name: String) {
        var n = name
        isCompressed = n.endsWith(".zip")
        if (isCompressed) n = n.removeSuffix(".zip")
        var ext = n.substringAfterLast(".", "")
        if (ext.length == 0 ) ext = "zip"

        rawExtension = ext
        singleExtension = if (rawExtension.length == 0) "" else ".$rawExtension"
        doubleExtension = if (isCompressed && (rawExtension != "zip")) "$singleExtension.zip" else singleExtension

        isImage = IMAGE_EXTENSIONS.contains(rawExtension)
    }

    constructor(file: File) : this(file.name)
}
