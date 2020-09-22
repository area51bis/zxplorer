package com.ses.app.zxdb.dao

import com.ses.app.sql.Column
import com.ses.app.sql.Key

open class GenericType<T> {
    @Key @Column var id: T? = null
    @Column var text: String? = null

    override fun toString(): String {
        return "$id, $text"
    }
}