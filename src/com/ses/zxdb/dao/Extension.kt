package com.ses.zxdb.dao

import com.ses.sql.Column
import com.ses.sql.Key
import com.ses.sql.Table

@Table("extensions")
class Extension {
    companion object {
        const val BMP = ".bmp"
        const val GIF = ".gif"
        const val JPG = ".jpg"
        const val PNG = ".png"
        const val SCR = ".scr"

        val IMAGE_EXTENSIONS = arrayOf(BMP, GIF, JPG, PNG, SCR)
    }

    @Key @Column
    lateinit var ext: String

    @Column
    lateinit var text: String
}
