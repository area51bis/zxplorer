package com.ses.app.zxlauncher

import com.ses.net.Http
import com.ses.zxdb.dao.Download
import com.ses.zxdb.fullUrl
import java.io.File

class DownloadManager {
    var workingDir: File = File(System.getProperty("user.dir"))

    fun download(download: Download, completion: (file: File) -> Unit) {
        Http().apply {
            val file = File(workingDir, download.file_link!!)
            file.parentFile.mkdirs()
            request = download.fullUrl

            println(request)
            getFile(file) { status, progress ->
                if (progress == 1.0f) {
                    println("Completed")
                    completion(file)
                } else {
                    print("Downloading... $progress\r");
                }
            }
        }
    }
}