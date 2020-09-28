package com.ses.zxdb.dao

import com.ses.sql.Table

@Table("filetypes")
class FileType: EnumerationTable<Int>() {
    companion object {
        const val LOADING_SCREEN: Int = 1
        const val RUNNING_SCREEN: Int = 2
        const val OPENNING_SCREEN: Int = 3

        const val TAPE_IMAGE: Int = 8
        const val SNAPSHOT_IMAGE: Int = 10
        const val DISK_IMAGE: Int = 11
    }
}
