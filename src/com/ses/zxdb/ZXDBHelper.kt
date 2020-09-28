package com.ses.zxdb

import com.ses.zxdb.dao.*

val Entry.downloads: List<Download> get() = ZXDB.getDownloads(id)
val Entry.machineType: MachineType? get() = ZXDB.getTable(MachineType::class)[machinetype_id]

val Download.downloadServerUrl: String get() = ZXDB.getDownloadServerUrl(file_link!!)
val Download.fileName: String get() = file_link!!.substringAfterLast("/")
val Download.fileExtension: String get() = fileName.let { it.substring(it.indexOf(".")) }
val Download.fileType: FileType? get() = ZXDB.getTable(FileType::class)[filetype_id]
val Download.formatType: Extension? get() = ZXDB.getTable(Extension::class)[fileExtension]
val Download.machineType: MachineType? get() = ZXDB.getTable(MachineType::class)[machinetype_id]
