package com.ses.zxdb

import com.ses.sql.SQL
import com.ses.zxdb.dao.Entry
import com.ses.zxdb.dao.FileType
import com.ses.zxdb.dao.GenreType
import com.ses.zxdb.dao.MachineType
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

class ZXDB {
    companion object {
        const val DB_NAME = "ZXDB.db"

        val instance by lazy {
            ZXDB()
        }
    }

    private val tables: HashMap<KClass<*>, Table<*>> = HashMap()

    private var conn: Connection = DriverManager.getConnection("jdbc:sqlite:$DB_NAME")

    protected fun finalize() {
        conn.close()
    }

    fun load() {
        readTable(MachineType::class)
        readTable(FileType::class)
        readTable(GenreType::class)
        readTable(Entry::class)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getTable(cls: KClass<T>): Table<T> {
        return (tables[cls] ?: readTable(cls)) as Table<T>
    }

    fun getGenre(genreId: Int): GenreType? {
        return getTable(GenreType::class)[genreId]
    }

    private fun <T : Any> readTable(cls: KClass<T>): Table<T> {
        val table = Table<T>(cls)
        SQL(conn).fetch(cls, table::addRow)
        tables[cls] = table
        return table
    }
}