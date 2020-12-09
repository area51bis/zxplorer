package com.ses.app.zxplorer.model.local

class NameParser(private val download: LocalModelDownload) {
    val baseName: String by lazy { extractBaseName() }
    val title: String by lazy { extractTitle() }

    val is128: Boolean by lazy {
        val name = download.getFileName()
        Regex("128k?", RegexOption.IGNORE_CASE).containsMatchIn(name)
    }
    private val articles = arrayOf("the", "el", "la", "los", "las")

    private fun extractBaseName(): String {
        var name = download.getFileName()
        name = name.removeSuffix(download.modelExtension.doubleExtension)

        val i = name.indexOfAny(charArrayOf('(', '_'))
        return if (i != -1) name.substring(0, i) else name
    }

    private fun extractTitle(): String {
        val sb = StringBuilder(baseName.length)

        var mode: Int = 0
        for (ch in baseName) {
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
                        sb.append(' ').append(ch)
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

        for (s in articles) {
            val suffix = " $s"
            if (sb.endsWith(suffix, true)) {
                sb.setLength(sb.length - suffix.length)
                sb.insert(0, "$s ")
                break
            }
        }

        return sb.toString().capitalize()
    }
}