package com.ses.app.zxlauncher

import com.ses.app.zxdb.dao.Entry
import com.ses.app.zxdb.dao.GenreType
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.TreeItem

class TreeCategory : TreeItem<String> {
    private val genreId: Int?
    val entries: ObservableList<Entry> by lazy {  FXCollections.observableArrayList() }
    //val entries: ArrayList<Entry> by lazy { ArrayList() }

    constructor(name: String) : super(name) {
        genreId = null
    }

    constructor(genre: GenreType) : super(genre.text) {
        genreId = genre.id
    }

    fun addEntry(e: Entry) {
        entries.add(e)
    }
}
