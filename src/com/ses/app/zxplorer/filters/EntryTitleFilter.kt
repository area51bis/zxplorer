package com.ses.app.zxplorer.filters

import com.ses.app.zxplorer.model.ModelEntry

class EntryTitleFilter : Filter<ModelEntry> {
    var text: String?
        set(value) {
            regex = if ((value != null) && (value.isNotEmpty())) {
                (if (isRegEx) value else ".*${Regex.escape(value)}.*").toRegex(RegexOption.IGNORE_CASE)
            } else {
                null
            }
        }
    private val isRegEx: Boolean
    private var regex: Regex? = null

    constructor(text: String?, isRegEx: Boolean = false) : super() {
        this.text = text
        this.isRegEx = isRegEx
    }

    override fun check(o: ModelEntry): Boolean {
        return regex?.matches(o.getTitle()) ?: true
    }
}