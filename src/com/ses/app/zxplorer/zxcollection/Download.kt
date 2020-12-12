package com.ses.app.zxplorer.zxcollection

import com.google.gson.annotations.SerializedName
import java.net.URLDecoder

class Download {
    lateinit var link: String
    @SerializedName("ext")
    var extension: String? = null
    var type: FileType? = null
    var name: String? = null
    @SerializedName("release_date")
    var releaseDate: ReleaseDate? = null
    var machine: Machine? = null

    @Exclude
    val fileName: String get() = name
            ?: URLDecoder.decode(link, "utf-8").substringAfterLast('/').substringBeforeLast('?')
}
