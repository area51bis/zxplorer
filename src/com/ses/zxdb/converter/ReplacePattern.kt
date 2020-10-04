package com.ses.zxdb.converter

class ReplacePattern {
    val pattern: Regex
    val text: String

    constructor(regex: String, text: String): this(regex.toRegex(), text)

    constructor(pattern: Regex, text: String) {
        this.pattern = pattern
        this.text = text
    }

    fun replace(input: CharSequence): String {
        return pattern.replace(input, text)
    }
}