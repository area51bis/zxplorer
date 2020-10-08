package com.ses.zxdb.dao

import com.ses.sql.Table

@Table("availabletypes")
class AvailableType: EnumerationTable<String>() {
    companion object {
        const val AVAILABLE = "A"
        const val DISTRIBUTION_DENIED = "D"
        const val DISTRIBUTION_DENIED_STILL_FOR_SALE = "d"
        const val MIA = "?"
        const val NEVER_RELEASED = "N"
        const val NEVER_RELEASED_RECOVERED = "R"
    }
}
