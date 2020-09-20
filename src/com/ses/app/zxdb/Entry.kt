package com.ses.app.zxdb

import com.ses.app.sql.Column
import com.ses.app.sql.Key
import com.ses.app.sql.Table

@Table("entries")
class Entry {
    @Key @Column var id: Int? = null
    @Column var title: String? = null

    @Column var library_title: String? = null
    @Column var original_id: Int? = null
    @Column var is_mod: Boolean? = null
    @Column var is_xrated: Boolean? = null
    @Column var is_crap: Boolean? = null
    @Column var was_inspired: Boolean? = null
    @Column var license_id: Int? = null
    @Column var machinetype_id: Byte? = null
    @Column var max_players: Byte? = null
    @Column var genretype_id: Byte? = null
    @Column var spot_genretype_id: Byte? = null
    @Column var publicationtype_id: Char? = null
    @Column var spanish_price: String? = null
    @Column var microdrive_price: String? = null
    @Column var disk_price: String? = null
    @Column var cartridge_price: String? = null
    @Column var availabletype_id: Char? = null
    @Column var known_errors: String? = null
    @Column var comments: String? = null
    @Column var spot_comments: String? = null
    @Column var hardware_blurb: String? = null
    @Column var hardware_feature: String? = null
    @Column var without_load_screen: Boolean? = null
    @Column var without_inlay: Boolean? = null
    @Column var hide_from_stp: Boolean? = null
    @Column var idiom_id: String? = null
    @Column var mag_ratings: String? = null
    @Column var issue_id: Int? = null
    @Column var book_isbn: String? = null
    @Column var book_pages: String? = null
}
