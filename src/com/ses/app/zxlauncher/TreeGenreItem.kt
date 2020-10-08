package com.ses.app.zxlauncher

import com.ses.app.zxlauncher.model.EntryRow
import com.ses.zxdb.dao.GenreType
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.TreeItem

class TreeGenreItem : TreeItem<String> {
    private val genreId: Int?
    val entries: ObservableList<EntryRow> by lazy {  FXCollections.observableArrayList() }

    constructor(name: String) : super(name) {
        genreId = null
    }

    constructor(genre: GenreType) : super(genre.text) {
        genreId = genre.id
    }

    fun addEntry(e: EntryRow) {
        entries.add(e)
    }
}
