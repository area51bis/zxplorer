package com.ses.zxdb.dao

import com.ses.sql.Column
import com.ses.sql.Key

open class EnumerationTable<T : Any> {
    @Key @Column lateinit var id: T
    @Column lateinit var text: String

    override fun toString(): String {
        return "$id, $text"
    }
}