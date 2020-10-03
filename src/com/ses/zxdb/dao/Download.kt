package com.ses.zxdb.dao

import com.ses.sql.Column
import com.ses.sql.Table

@Table("downloads")
class Download {
    @Column var id: Int = 0
    @Column var entry_id: Int = 0
    @Column var release_seq: Int = 0
    @Column lateinit var file_link: String
    @Column var file_date: String? = null
    @Column var file_size: Int? = null
    @Column var file_md5: String? = null
    @Column var filetype_id: Int = 0
    @Column var scr_border: Int = 7
    @Column var language_id: String? = null
    @Column var is_demo: Int = 0
    @Column var schemetype_id: String? = null
    @Column var machinetype_id: Int? = null
    @Column var file_code: String? = null
    @Column var file_barcode: String? = null
    @Column var file_dl: String? = null
    @Column var sourcetype_id: String? = null
    @Column var release_year: Int? = null
    @Column var comments: String? = null
}
