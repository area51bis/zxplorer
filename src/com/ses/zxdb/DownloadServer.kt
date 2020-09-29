package com.ses.zxdb

class DownloadServer(val prefix: String, val url: String) {
    fun getServerUrl(url: String) = this.url + url.substring(prefix.length)
}