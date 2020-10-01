package com.ses.app.zxlauncher

import com.ses.net.Http
import com.ses.zxdb.dao.Download
import com.ses.zxdb.fullUrl
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

        Http().apply {
            file.parentFile.mkdirs()
            request = download.fullUrl

            getFile(file) { status, progress ->
                if (progress == 1.0f) {
                    completion(file)
                }
            }
        }
    }

    fun exists(download: Download): Boolean = getFile(download).exists()
}