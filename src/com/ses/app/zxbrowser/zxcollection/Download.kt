package com.ses.app.zxbrowser.zxcollection

import com.google.gson.annotations.SerializedName

class Download {
    @SerializedName("link")
    lateinit var fileLink: String
    @SerializedName("ext")
    var extension: String? = null
    @SerializedName("type")
    var fileType: FileType? = null
    @SerializedName("name")
    var fileName: String? = null
    @SerializedName("release_date")
    var releaseDate: ReleaseDate? = null
    var machine: Machine? = null
}
