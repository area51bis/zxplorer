package com.ses.zxdb.converter


class InsertParser : SentenceParser() {
    companion object {
        private val TABLE_REPLACES = arrayOf(
                ReplacePattern(INTEGER_PATTERN, "INTEGER"),
                ReplacePattern(TEXT_PATTERN, "TEXT"),
                ReplacePattern(ENGINE_PATTERN, ""),
                ReplacePattern(CHARACTER_SET_PATTERN, ""),
                ReplacePattern(UTF8_PATTERN, "RTRIM"))
    }

    val KEY_PATTERN = Regex("^\\s*(?:UNIQUE\\s*)?KEY")
    private val IGNORED_LINES = arrayOf(
            KEY_PATTERN
    )

    val sb = StringBuilder()

    override fun parse(converter: MySQLConverter): Boolean {
        var beginInsert: String
        var rowCount = 0

        if (converter.line!!.startsWith("INSERT INTO")) {
            beginInsert = "${converter.line}\n"
            sb.setLength(0);
            sb.append(beginInsert);

            while ((converter.nextLine() != null)) {
                ++rowCount;
                sb.append(converter.line!!.replace("\\'", "''")).append("\n")
                if (match(converter.line!!, END_STAMENT_PATTERN)) {
                    //removeLastComma()
                    insertRows(converter)
                    break;
                }

                if (rowCount == 100) {
                    removeLastComma()
                    sb.append(';')
                    insertRows(converter)

                    rowCount = 0
                    sb.setLength(0)
                    sb.append(beginInsert)
                }
            }
        }

        return false
    }

    private fun removeLastComma() {
        var i = sb.length - 1
        var ch = 0.toChar()
        while (i >= 0 && Character.isWhitespace(sb[i].also { ch = it })) --i
        if (ch == ',') sb.setLength(i)
    }

    private fun insertRows(converter: MySQLConverter) {
        val sql = sb.toString()
        try {
        converter.conn.createStatement().use { it.execute(sql) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}