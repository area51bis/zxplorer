package com.ses.app.zxlauncher

import com.ses.zxdb.ZXDB
import com.ses.zxdb.dao.Entry
import com.ses.zxdb.dao.GenreType

object ZXDBUtil {
    fun getCategoryPath(name: String?): List<String> {
        return name
                ?.replace("(.*) Game:".toRegex(), "Game: \$1:")
                ?.split(": ?".toRegex())
                ?: emptyList()
    }

    fun getCategoryPath(entry: Entry): List<String> {
        return getCategoryPath(ZXDB.getGenre(entry.genretype_id)?.text)
    }

    fun getCategoryPath(genre: GenreType): List<String> {
        return getCategoryPath(genre.text)
    }
}