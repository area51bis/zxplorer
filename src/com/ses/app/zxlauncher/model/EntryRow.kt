package com.ses.app.zxlauncher.model

import com.ses.sql.Column
import com.ses.sql.Query
import com.ses.zxdb.*
import com.ses.zxdb.dao.*
import java.util.*
import kotlin.collections.ArrayList

@Query("SELECT r.release_year, r.release_month, e.* FROM entries e INNER JOIN releases r WHERE r.entry_id=e.id AND r.release_seq=0")
class EntryRow {
    @Column("id") var id: Int = 0
    @Column("title") lateinit var title: String

    //@Column lateinit var library_title: String
    //@Column var is_xrated: Boolean = false
    @Column("machinetype_id") var machineTypeId: Int? = null
    @Column("max_players") var maxPlayers: Int = 1
    @Column("genretype_id") var genreTypeId: Int? = null

    //@Column var spot_genretype_id: Int? = null
    //@Column var publicationtype_id: String? = null
    @Column("availabletype_id") var availableTypeId: String? = null

    //@Column var without_load_screen: Boolean = false
    //@Column var without_inlay: Boolean = false
    //@Column var hide_from_stp: Boolean = false
    @Column("language_id") var languageId: String? = null
    //val magRatings = entry.mag_ratings
    //@Column var issue_id: Int? = null
    //@Column var book_isbn: String? = null
    //@Column var book_pages: String? = null

    @Column("release_year") var releaseYear: Int? = null
    @Column("release_month") var releaseMonth: Int? = null
    @Column("release_day") var releaseDay: Int? = null
    val releaseYearString: String get() = releaseYear?.toString() ?: Model.NULL_YEAR_STRING

    val releaseDate: ReleaseDate by lazy { ReleaseDate(releaseYear, releaseMonth, releaseDay) }

    val machineType: MachineType? by lazy { ZXDB.getTable(MachineType::class)[machineTypeId] }
    val machineTypeString: String get() = machineType?.text ?: Model.NULL_MACHINE_TYPE_STRING
    val genreType: GenreType? by lazy { ZXDB.getTable(GenreType::class)[genreTypeId] }
    val availableType: AvailableType? by lazy { ZXDB.getTable(AvailableType::class)[availableTypeId] }
    val availabilityString: String get() = availableType?.text ?: Model.NULL_AVAILABLE_STRING
    val language: Language? by lazy { ZXDB.getTable(Language::class)[languageId] }

    val releases: List<Release> by lazy {
        ArrayList<Release>().also { list ->
            ZXDB.sql().select("SELECT * FROM releases WHERE entry_id=$id ORDER BY release_seq", Release::class) {
                list.add(it)
            }
        }
    }

    val downloads: List<Download> by lazy {
        ArrayList<Download>().also { list ->
            ZXDB.sql().select("SELECT * FROM downloads WHERE entry_id=$id ORDER BY release_seq", Download::class) {
                list.add(it)
            }
        }
    }

    val categoryName: String get() = genreType?.text ?: Model.NULL_GENRE_STRING

    val categoryPath: List<String> get() = Model.getCategoryPath(genreType)
}
