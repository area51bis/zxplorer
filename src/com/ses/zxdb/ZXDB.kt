package com.ses.zxdb

import com.ses.sql.SQL
import com.ses.zxdb.dao.*
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import kotlin.reflect.KClass

object ZXDB {
    const val DB_NAME = "ZXDB.db"

    private const val ARCHIVE_ORG = "https://archive.org/download/World_of_Spectrum_June_2017_Mirror/World%20of%20Spectrum%20June%202017%20Mirror.zip/World%20of%20Spectrum%20June%202017%20Mirror/sinclair/"
    private const val SPECTRUM_COMPUTING_ORG = "https://spectrumcomputing.co.uk/zxdb/sinclair/"

    private val DOWNLOAD_SERVERS = arrayOf(
            DownloadServer("wos", "/pub/sinclair/", ARCHIVE_ORG),
            DownloadServer("spectrumcomputing", "/zxdb/sinclair/", SPECTRUM_COMPUTING_ORG)
    )
    private val tables: HashMap<KClass<*>, Table<*>> = HashMap()

    private lateinit var conn: Connection

    var isOpened: Boolean = false
        private set

    fun open(): Boolean = try {
        if (File(DB_NAME).exists()) {
            conn = DriverManager.getConnection("jdbc:sqlite:$DB_NAME")
            //TODO comprobar que la base de datos es válida
            isOpened = true
            true
        } else {
            isOpened = false
            false
        }
    } catch (e: Exception) {
        isOpened = false
        false
    }

    fun close() {
        try {
            conn.close()
        } catch (e: Exception) {
        }
        tables.clear()
        isOpened = false
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

    fun getDownloadServer(url: String): DownloadServer? {
        for (server in DOWNLOAD_SERVERS) {
            if (url.startsWith(server.prefix)) {
                return server
            }
        }

        return null
    }

    fun getDownloadServerUrl(url: String): String = getDownloadServer(url)?.getServerUrl(url) ?: url

    private fun <T : Any> readTable(cls: KClass<T>): Table<T> {
        val table = Table<T>(cls)
        sql().select(cls = cls, f = table::addRow)
        tables[cls] = table
        return table
    }
}