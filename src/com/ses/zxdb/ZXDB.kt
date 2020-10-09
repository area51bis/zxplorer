package com.ses.zxdb

import com.ses.sql.SQL
import com.ses.zxdb.dao.*
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import kotlin.reflect.KClass

/*
Descargas:

SELECT e.id, e.title, ft.text, fmt.text, d.file_link
FROM entries e
INNER JOIN downloads d ON d.entry_id=e.id
INNER JOIN filetypes ft ON ft.id=d.filetype_id
INNER JOIN formattypes fmt ON fmt.id==d.formattype_id
WHERE e.id=48
*/

/*
Releases:

SELECT e.title, r.release_seq, r.release_year, r.release_month, r.release_day, r.release_price
FROM entries e
INNER JOIN releases r ON r.entry_id=e.id
WHERE e.id=3012 -- AND r.release_seq=0
ORDER BY release_seq
*/

object ZXDB {
    const val DB_NAME = "ZXDB.db"

    //val ARCHIVE_ORG = "https://archive.org/download/World_of_Spectrum_June_2017_Mirror/World of Spectrum June 2017 Mirror.zip/World of Spectrum June 2017 Mirror/sinclair"
    private const val ARCHIVE_ORG = "https://archive.org/download/World_of_Spectrum_June_2017_Mirror/World%20of%20Spectrum%20June%202017%20Mirror.zip/World%20of%20Spectrum%20June%202017%20Mirror/sinclair/"
    private const val SPECTRUM_COMPUTING_ORG = "https://spectrumcomputing.co.uk/zxdb/sinclair/"
    // las pantallas (carga y juego) parecen estar en SPECTRUM_COMPUTING_ORG

    private val DOWNLOAD_SERVERS = arrayOf(
            DownloadServer("/pub/sinclair/", ARCHIVE_ORG),
            DownloadServer("/zxdb/sinclair/", SPECTRUM_COMPUTING_ORG)
    )

    /*
    private var LOAD_TABLES = arrayOf(
            // enumeration tables
            Extension::class,
            FileType::class,
            GenreType::class,
            MachineType::class,

            Entry::class
    )
    */
    private val tables: HashMap<KClass<*>, Table<*>> = HashMap()

    private lateinit var conn: Connection

    /*
    protected fun finalize() {
        conn.close()
    }
    */

    fun open(): Boolean = try {
        if (File(DB_NAME).exists()) {
            conn = DriverManager.getConnection("jdbc:sqlite:$DB_NAME")
            //for (t in LOAD_TABLES) readTable(t)
            //TODO comprobar que la base de datos es válida
            true
        } else {
            false
        }
    } catch (e: Exception) {
        false
    }

    fun close() {
        try {
            conn.close()
        } catch (e: Exception) {
        }
        tables.clear();
    }

    fun sql(): SQL = SQL(conn)

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getTable(cls: KClass<T>): Table<T> {
        return (tables[cls] ?: readTable(cls)) as Table<T>
    }

    fun getGenre(genreId: Int?): GenreType? {
        return getTable(GenreType::class)[genreId]
    }

    fun getDownloads(entryId: Int): List<Download> {
        val list = ArrayList<Download>()
        SQL(conn).select(where = "entry_id = $entryId", cls = Download::class, f = list::add)
        return list
    }

    fun getDownloadServerUrl(url: String): String {
        for (server in DOWNLOAD_SERVERS) {
            if (url.startsWith(server.prefix)) {
                return server.getServerUrl(url)
            }
        }

        return url
    }

    private fun <T : Any> readTable(cls: KClass<T>): Table<T> {
        val table = Table<T>(cls)
        SQL(conn).select(cls = cls, f = table::addRow)
        tables[cls] = table
        return table
    }
}