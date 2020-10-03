package com.ses.zxdb.dao

import com.ses.sql.Column
import com.ses.sql.Key
import com.ses.sql.Table

@Table("extensions")
class Extension {
    @Key @Column
    lateinit var ext: String

    @Column
    lateinit var text: String
}
