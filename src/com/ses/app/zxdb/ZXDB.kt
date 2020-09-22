package com.ses.app.zxdb

import com.ses.app.sql.SQL
import com.ses.app.zxdb.dao.Entry
import com.ses.app.zxdb.dao.FileType
import com.ses.app.zxdb.dao.GenreType
import com.ses.app.zxdb.dao.MachineType
import java.sql.Connection
import java.sql.DriverManager
import kotlin.reflect.KClass

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

    fun getGenre(genreId: Int?): GenreType? {
        return if (genreId != null) {
            getTable(GenreType::class)[genreId]
        } else {
            null
        }
    }

    private fun <T : Any> readTable(cls: KClass<T>): Table<T> {
        val table = Table<T>(cls)
        SQL(conn).fetch(cls, table::addRow)
        tables[cls] = table
        return table
    }
}