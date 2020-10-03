package com.ses.zxdb

import com.ses.zxdb.dao.*

val Entry.downloads: List<Download> get() = ZXDB.getDownloads(id)
val Entry.genre: GenreType? get() = ZXDB.getGenre(genretype_id)
val Entry.machineType: MachineType? get() = ZXDB.getTable(MachineType::class)[machinetype_id]

val Download.fullUrl: String get() = ZXDB.getDownloadServerUrl(file_link)
val Download.fileName: String get() = file_link.substringAfterLast("/")
// val Download.fileExtension: String? get() = extension?.ext
val Download.fileType: FileType get() = ZXDB.getTable(FileType::class)[filetype_id]!!
val Download.extension: Extension? get() = ZXDB.getTable(Extension::class).rows.firstOrNull { file_link.endsWith(it.ext) }
val Download.machineType: MachineType? get() = ZXDB.getTable(MachineType::class)[machinetype_id]

/** `".txz.zip"` -> `"tzx"`. */
val Extension.rawExtension: String get() = ext.removeSuffix(".zip").removePrefix(".")