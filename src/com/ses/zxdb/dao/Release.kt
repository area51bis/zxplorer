package com.ses.zxdb.dao

import com.ses.sql.Column
import com.ses.sql.Table

@Table("releases")
class Release {
    @Column var entry_id: Int = 0
    @Column var release_seq: Int = 0
    @Column var release_year: Int? = null
    @Column var release_month: Int? = null
    @Column var release_day: Int? = null
    @Column var release_price: String? = null
    @Column var budget_price: String? = null
    @Column var microdrive_price: String? = null
    @Column var disk_price: String? = null
    @Column var cartridge_price: String? = null
}