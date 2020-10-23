package com.ses.app.zxlauncher.model.local

import com.ses.app.zxlauncher.model.*
import java.io.File

class LocalModelEntry : ModelEntry {
    constructor() : super()
    constructor(model: Model) : super(model)

    lateinit var key: String
    private lateinit var _title: String
    private val _releaseDate = ReleaseDate()

    private val _downloads = ArrayList<ModelDownload>()

    companion object {
        fun getFileKey(file: File): String {
            var name = file.name

            val ext = ModelFileExtension(file)
            name = name.removeSuffix(ext.doubleExtension)

            val i = name.indexOf('(')
            return if (i != -1) name.substring(0, i) else name
        }
    }

    fun addFile(file: File) {
        if (_downloads.isEmpty()) {
            key = getFileKey(file)
            _title = extractTitle(key)
        }
        _downloads.add(LocalModelDownload(model, file))
    }

    private fun extractTitle(s: String): String {
        val sb = StringBuilder(s.length)

        var mode: Int = 0
        for (ch in s) {
            //minúsculas
            when (mode) {
                0 -> when { // mayúsculas
                    ch.isUpperCase() -> sb.append(ch)
                    ch.isLetter() -> {
                        sb.append(ch)
                        mode = 1
                    }
                    ch.isDigit() -> {
                        sb.append(' ').append(ch)
                        mode = 2
                    }
                    else -> if (sb.last() != ' ') sb.append(' ')
                }

                1 -> when { // minúsculas
                    ch.isLowerCase() -> sb.append(ch)
                    ch.isUpperCase() -> {
                        sb.append(ch)
                        mode = 0
                    }
                    ch.isDigit() -> {
                        sb.append(' ').append(ch)
                        mode = 2
                    }
                    else -> {
                        if (sb.last() != ' ') sb.append(' ')
                        mode = 0
                    }
                }

                2 -> when { // número
                    ch.isDigit() -> sb.append(ch)
                    ch.isUpperCase() -> {
                        sb.append(' ').append(ch)
                        mode = 0
                    }
                    ch.isLowerCase() -> {
                        sb.append(' ').append(ch)
                        mode = 1
                    }
                    else -> {
                        if (sb.last() != ' ') sb.append(' ')
                        mode = 0
                    }
                }
            }
        }

        return sb.toString()
    }

    override fun getTitle(): String = _title
    override fun getGenre(): String = ""
    override fun getReleaseYear(): Int? = null
    override fun getReleaseDate(): ReleaseDate = _releaseDate
    override fun getMachine(): String = ""
    override fun getAvailability(): String = ""
    override fun getDownloads(): List<ModelDownload> = _downloads
}
