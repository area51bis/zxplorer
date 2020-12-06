package com.ses.app.zxbrowser.zxcollection

import com.google.gson.annotations.SerializedName

class Download {
    @SerializedName("file_link")
    lateinit var fileLink: String
    var extension: String? = null
    @SerializedName("file_type")
    var fileType: FileType? = null
    @SerializedName("file_name")
    var fileName: String? = null
    @SerializedName("release_date")
    var releaseDate: ReleaseDate? = null
    var machine: Machine? = null
}
