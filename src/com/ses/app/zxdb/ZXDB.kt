package com.ses.app.zxdb

import com.ses.app.sql.SQL
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

    private var conn: Connection? = null

    fun open() {
        conn = DriverManager.getConnection("jdbc:sqlite:$DB_NAME")
    }

    fun close() {
        conn = null;
    }

    fun <T: Any> readTable(cls: KClass<T>) {
        conn?.let {
            SQL(it).fetch<T>(cls) { c ->
                println(c)
            }
        }
    }
}