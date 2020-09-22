package com.ses.app.zxlauncher

import com.ses.app.zxdb.ZXDB
import com.ses.app.zxdb.dao.Entry
import com.ses.app.zxdb.dao.GenreType

class ZXDBUtil {
    companion object {
        fun getCategoryPath(name: String?): List<String> {
            return name
                    ?.replace("(.*) Game:".toRegex(), "Game: \$1:")
                    ?.split(": ?".toRegex())
                    ?: emptyList()
        }

        fun getCategoryPath(entry: Entry): List<String> {
            return getCategoryPath(ZXDB.instance.getGenre(entry.genretype_id)?.text)
        }

        fun getCategoryPath(genre: GenreType): List<String> {
            return getCategoryPath(genre.text)
        }
    }
}