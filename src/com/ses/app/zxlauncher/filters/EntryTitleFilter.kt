package com.ses.app.zxlauncher.filters

import com.ses.zxdb.dao.Entry

class EntryTitleFilter : Filter<Entry> {
    var text: String?
        set(value) {
            regex = if ((value != null) && (value.isNotEmpty())) {
                (if (isRegEx) value else ".*${Regex.escape(value)}.*").toRegex(RegexOption.IGNORE_CASE)
            } else {
                null
            }
            value
        }
    private val isRegEx: Boolean
    private var regex: Regex? = null

    constructor(text: String?, isRegEx: Boolean = false) : super() {
        this.text = text
        this.isRegEx = isRegEx
    }

    override fun check(o: Entry): Boolean {
        return regex?.matches(o.title!!) ?: true
    }
}