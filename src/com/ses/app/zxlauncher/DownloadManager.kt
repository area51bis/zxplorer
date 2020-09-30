package com.ses.app.zxlauncher

import com.ses.net.Http
import com.ses.zxdb.dao.Download
import com.ses.zxdb.fullUrl
import java.io.File

class DownloadManager {
    var workingDir: File = File(System.getProperty("user.dir"))

    fun getFile(download: Download): File = File(workingDir, download.file_link!!)

    fun download(download: Download, completion: (file: File) -> Unit) {
        val file = getFile(download)

        if (file.exists()) {
            completion(file)
            return
        }

        Http().apply {
            file.parentFile.mkdirs()
            request = download.fullUrl

            println(request)
            getFile(file) { status, progress ->
                if (progress == 1.0f) {
                    completion(file)
                }
            }
        }
    }

    fun exists(download: Download): Boolean = getFile(download).exists()
}