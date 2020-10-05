package com.ses.zxdb.converter

class CreateTableParser : SentenceParser() {
    companion object {
        private val TABLE_REPLACES = arrayOf(
                ReplacePattern(INTEGER_PATTERN, "INTEGER"),
                ReplacePattern(REAL_PATTERN, "REAL"),
                ReplacePattern(TEXT_PATTERN, "TEXT"),
                ReplacePattern(ROW_AUTO_INCREMENT_PATTERN, "PRIMARY KEY AUTOINCREMENT"),
                ReplacePattern(ENGINE_PATTERN, ""),
                ReplacePattern(CHARACTER_SET_PATTERN, ""),
                ReplacePattern(UTF8_PATTERN, "RTRIM"))

        val KEY_PATTERN = Regex("^\\s*(?:UNIQUE\\s*)?KEY")
        val CONSTRAINT_PATTERN = Regex("^\\s*CONSTRAINT")
        val PRIMARY_KEY_PATTERN = Regex("^\\s*PRIMARY KEY")
        private val IGNORED_LINES = arrayOf(
                KEY_PATTERN,
                CONSTRAINT_PATTERN
        )

        val TABLENAME_PATTERN = Regex("CREATE TABLE.* `(.*)`")
    }

    private val sb = StringBuilder()

    override fun parse(converter: MySQLConverter): Boolean {
        if (converter.line!!.startsWith("CREATE TABLE")) {
            TABLENAME_PATTERN.find(converter.line!!)?.also { m ->
                m.groups[1]?.value?.also { tableName ->
                    converter.executeSql("DROP TABLE IF EXISTS '$tableName'")
                    //println("Creating table: $tableName")
                    converter.notifyProgress(tableName)
                }
            }

            sb.setLength(0)
            sb.append(converter.line).append("\n")

            var primaryKeyDefined = false

            while (converter.nextLine() != null) {
                if (match(converter.line!!, END_STAMENT_PATTERN)) {
                    removeLastComma()
                    sb.append(");")
                    break
                }

                if (match(converter.line!!, IGNORED_LINES)) continue
                if (primaryKeyDefined && PRIMARY_KEY_PATTERN.containsMatchIn(converter.line!!)) continue

                val line = replacePatterns(converter.line!!, TABLE_REPLACES)
                sb.append(line).append('\n')

                if (!primaryKeyDefined && line.contains("PRIMARY KEY")) primaryKeyDefined = true;
            }

            val sql = BAD_COMMA.replace(sb.toString())
            converter.executeSql(sql)

            return true
        }

        return false
    }

    private fun removeLastComma() {
        var i = sb.length - 1
        var ch = 0.toChar()
        while (i >= 0 && Character.isWhitespace(sb[i].also { ch = it })) --i
        if (ch == ',') sb.setLength(i)
    }
}