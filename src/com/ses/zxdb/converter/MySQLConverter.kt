package com.ses.zxdb.converter

import java.io.BufferedReader
import java.io.File
import java.sql.Connection
import java.sql.DriverManager


class MySQLConverter(private val mysqlFile: String, private val sqliteFile: String) {
    lateinit var conn: Connection
    lateinit var input: BufferedReader
    var lineNumber = 0
    var line: String? = null

    private var callback: ((progress: Float, tableName: String) -> Unit)? = null
    private var totalBytes: Long = 0
    private var bytesReaded: Long = 0

    companion object {
        private val LINE_COMMENT_PATTERN = "^\\s*--".toRegex()
        private val parsers = arrayOf(
                CreateTableParser(),
                InsertParser())
    }

    fun convert(callback: ((progress: Float, tableName: String) -> Unit)? = null) {
        this.callback = callback

        val file = File(mysqlFile)
        totalBytes = file.length()
        input = file.bufferedReader()

        input.use {
            conn = DriverManager.getConnection("jdbc:sqlite:$sqliteFile")
            conn.use {
                conn.autoCommit = false

                lineNumber = 0
                while (nextLine() != null) {
                    if (line!!.isBlank()) continue
                    if (LINE_COMMENT_PATTERN.containsMatchIn(line!!)) continue

                    for (parser in parsers) {
                        if (parser.parse(this)) break
                    }
                }

                conn.commit()
            }
        }
    }

    fun nextLine(): String? {
        line = input.readLine()
        if (line != null) {
            ++lineNumber
            bytesReaded += line!!.length
        }
        return line
    }

    fun executeSql(sql: String) {
        conn.createStatement().use { it.execute(sql) }
    }

    lateinit var tableName: String

    fun notifyProgress(tableName: String? = null) {
        if (tableName != null) this.tableName = tableName
        callback?.invoke(bytesReaded.toFloat() / totalBytes, this.tableName)
    }
}