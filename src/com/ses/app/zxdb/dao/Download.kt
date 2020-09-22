package com.ses.app.zxdb.dao

import com.ses.app.sql.Column
import com.ses.app.sql.Table

@Table("downloads")
class Download {
    @Column var id: Int? = null;
    @Column var entry_id: Int? = null;
    @Column var release_seq: Int? = null;
    @Column var file_link: String? = null;
    @Column var file_date: String? = null;
    @Column var file_size: Int? = null;
    @Column var file_md5: String? = null;
    @Column var filetype_id: Int? = null;
    @Column var scr_border: Int? = null;
    @Column var language_id: String? = null;
    @Column var is_demo: Int? = null;
    @Column var schemetype_id: String? = null;
    @Column var machinetype_id: Int? = null;
    @Column var file_code: String? = null;
    @Column var file_barcode: String? = null;
    @Column var file_dl: String? = null;
    @Column var sourcetype_id: String? = null;
    @Column var release_year: Int? = null;
    @Column var comments: String? = null;
}
