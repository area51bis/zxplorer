package com.ses.app.zxdb

import com.ses.app.sql.Column
import com.ses.app.sql.Key
import com.ses.app.sql.Table

@Table("machinetypes")
class MachineType {
    @Key @Column var id: Int? = null
    @Column var text: String? = null

    override fun toString(): String {
        return "MachineType: $id, $text"
    }
}