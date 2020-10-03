package com.ses.zxdb.dao

import com.ses.sql.Column
import com.ses.sql.Key
import com.ses.sql.Table

@Table("entries")
class Entry {
    @Key @Column var id: Int = 0
    @Column lateinit var title: String
    @Column lateinit var library_title: String
    @Column var is_xrated: Boolean = false
    @Column var machinetype_id: Int? = null
    @Column var max_players: Int = 1
    @Column var genretype_id: Int? = null
    @Column var spot_genretype_id: Int? = null
    @Column var publicationtype_id: String? = null
    @Column var availabletype_id: String? = null
    @Column var without_load_screen: Boolean = false
    @Column var without_inlay: Boolean = false
    @Column var hide_from_stp: Boolean = false
    @Column var language_id: String? = null
    @Column var mag_ratings: String? = null
    @Column var issue_id: Int? = null
    @Column var book_isbn: String? = null
    @Column var book_pages: String? = null
}
