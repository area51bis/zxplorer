package com.ses.app.zxlauncher

import com.ses.app.zxlauncher.ui.ProgressDialog
import com.ses.net.Http
import com.ses.zxdb.dao.Download
import com.ses.zxdb.fileName
import com.ses.zxdb.fullUrl
import javafx.application.Platform
import java.io.File

class DownloadManager {
    var rootDir = App.workingDir // directorio raíz
    var downloadDir: File = File(rootDir, "zxdb") // directorio de descargas

    fun getFile(download: Download): File = File(downloadDir, download.file_link!!)

    fun download(download: Download, completion: (file: File) -> Unit) {
        val file = getFile(download)

        if (file.exists()) {
            completion(file)
            return
        }

        val dialog = ProgressDialog.create().apply {
            title = "Download"
            message = download.fileName
            show()
        }

        Http().apply {
            file.parentFile.mkdirs()
            request = download.fullUrl

            getFile(file) { status, progress ->
                when (status) {
                    Http.Status.Connecting -> Platform.runLater { dialog.message = "Connecting..." }
                    Http.Status.Connected -> Platform.runLater { dialog.message = "Downloading '${download.fileName}'..." }
                    Http.Status.Completed -> completion(file)
                }
            }
            Platform.runLater { dialog.hide() }
        }
    }

    fun exists(download: Download): Boolean = getFile(download).exists()
}