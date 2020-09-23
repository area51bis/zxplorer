package com.ses.zxdb.dao

import com.ses.sql.Column
import com.ses.sql.Key

open class GenericType<T : Any> {
    @Key @Column lateinit var id: T
    @Column var text: String? = null

    override fun toString(): String {
        return "$id, $text"
    }
}