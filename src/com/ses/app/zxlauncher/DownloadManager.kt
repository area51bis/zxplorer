package com.ses.app.zxlauncher

import com.ses.app.zxlauncher.model.ModelDownload
import com.ses.app.zxlauncher.ui.ProgressDialog
import com.ses.net.Http
import javafx.application.Platform
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class DownloadManager(val downloadDir: File) {
    fun getFile(download: ModelDownload): File = File(downloadDir, download.getLink())

    fun download(download: ModelDownload, completion: (file: File) -> Unit) {
        val file = getFile(download)

        if (file.exists()) {
            completion(file)
            return
        }

        val dialog = ProgressDialog.create().apply {
            title = T("download")
            message = download.getFileName()
            show()
        }

        GlobalScope.launch {
            Http().apply {
                file.parentFile.mkdirs()
                request = download.getFullUrl()

                getFile(file) { status, progress ->
                    when (status) {
                        Http.Status.Connecting -> Platform.runLater { dialog.message = T("connecting_") }
                        Http.Status.Connected -> Platform.runLater { dialog.message = T("downloading_fmt").format(download.getFileName()) }
                        Http.Status.Completed -> completion(file)
                    }
                }
                Platform.runLater { dialog.hide() }
            }
        }
    }

    fun exists(download: ModelDownload): Boolean = getFile(download).exists()
}