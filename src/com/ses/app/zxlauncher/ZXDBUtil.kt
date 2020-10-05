package com.ses.app.zxlauncher

import com.ses.app.zxlauncher.ui.ProgressDialog
import com.ses.net.Http
import com.ses.zxdb.ZXDB
import com.ses.zxdb.converter.MySQLConverter
import com.ses.zxdb.dao.Entry
import com.ses.zxdb.dao.GenreType
import javafx.application.Platform
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

object ZXDBUtil {
    fun getCategoryPath(name: String?): List<String> {
        return name
                ?.replace("(.*) Game:".toRegex(), "Game: \$1:")
                ?.split(": ?".toRegex())
                ?: emptyList()
    }

    fun getCategoryPath(entry: Entry): List<String> {
        return getCategoryPath(ZXDB.getGenre(entry.genretype_id)?.text)
    }

    fun getCategoryPath(genre: GenreType): List<String> {
        return getCategoryPath(genre.text)
    }

    enum class UpdateStatus {
        Connecting,
        Downloading,
        Converting,
        Completed,
        Error
    }

    fun updateDatabase(progressHandler: ((status: UpdateStatus, progress: Float, message: String) -> Unit)?) {
        val workingDir = File(System.getProperty("user.dir"))
        val mySqlFile = File(workingDir, "ZXDB_mysql.sql")
        val sqliteFile = File(workingDir, ZXDB.DB_NAME)
        val sqliteTempFile = File(workingDir, "_${ZXDB.DB_NAME}_")

        GlobalScope.launch {
            // descargar ZXDB_mysql.sql
            Http().apply {
                request = "https://github.com/zxdb/ZXDB/raw/master/ZXDB_mysql.sql"
                getFile(mySqlFile) { status, progress ->
                    when (status) {
                        Http.Status.Connecting -> progressHandler?.invoke(UpdateStatus.Connecting, 0.0f, "Connecting")
                        Http.Status.Connected -> progressHandler?.invoke(UpdateStatus.Downloading, 0.0f, "Downloading")
                        Http.Status.Downloading -> progressHandler?.invoke(UpdateStatus.Downloading, progress, "Downloading")
                        //Http.Status.Completed
                        Http.Status.Error -> progressHandler?.invoke(UpdateStatus.Error, progress, "Error")
                    }
                }
            }

            // convertir a sqlite
            progressHandler?.invoke(UpdateStatus.Converting, 0.0f, "Converting")
            MySQLConverter(mySqlFile.absolutePath, sqliteTempFile.absolutePath).convert { progress, tableName ->
                progressHandler?.invoke(UpdateStatus.Converting, progress, "Converting table $tableName...")
            }

            // sustituir fichero
            ZXDB.close()
            sqliteFile.delete()
            sqliteTempFile.renameTo(sqliteFile)

            // recargar base de datos y refrescar los datos
            ZXDB.open()

            progressHandler?.invoke(UpdateStatus.Completed, 1.0f, "Completed")
        }
    }
}