package com.ses.zxdb

import com.ses.zxdb.dao.Download
import com.ses.zxdb.dao.Entry
import com.ses.zxdb.dao.FileType
import com.ses.zxdb.dao.MachineType

val Entry.downloads: List<Download> get() = ZXDB.getDownloads(id)
val Entry.machineType: MachineType? get() = ZXDB.getTable(MachineType::class)[machinetype_id]

val Download.downloadServerUrl: String get() = ZXDB.getDownloadServerUrl(file_link!!)
val Download.fileType: FileType? get() = ZXDB.getTable(FileType::class)[filetype_id]
val Download.machineType: MachineType? get() = ZXDB.getTable(MachineType::class)[machinetype_id]
