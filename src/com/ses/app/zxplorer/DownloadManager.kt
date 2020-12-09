package com.ses.app.zxplorer

import com.ses.app.zxplorer.model.ModelDownload
import com.ses.app.zxplorer.ui.ProgressDialog
import com.ses.net.Http
import javafx.application.Platform
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class DownloadManager() {
    fun download(url: String, file: File, completion: (file: File?) -> Unit) {
        if (file.exists()) {
            completion(file)
            return
        }

        val dialog = ProgressDialog.create().apply {
            title = T("download")
            message = file.name
            show()
        }

        GlobalScope.launch {
            Http().apply {
                file.parentFile.mkdirs()
                request = url

                getFile(file) { status, progress ->
                    when (status) {
                        Http.Status.Connecting -> Platform.runLater { dialog.message = T("connecting_") }
                        Http.Status.Connected -> Platform.runLater { dialog.message = T("downloading_fmt").format(file.name) }
                        Http.Status.Completed -> completion(file)
                        Http.Status.Error -> completion(null)
                    }
                }
                Platform.runLater { dialog.hide() }
            }
        }
    }

    fun exists(download: ModelDownload): Boolean = download.getFile().exists()
}