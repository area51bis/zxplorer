package com.ses.zxdb

class DownloadServer(val id:String, val prefix: String, val url: String) {
    fun getServerUrl(url: String) = this.url + url.substring(prefix.length)
}