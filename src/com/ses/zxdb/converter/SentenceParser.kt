package com.ses.zxdb.converter

import java.io.IOException
import java.sql.SQLException


abstract class SentenceParser {
    @Throws(IOException::class, SQLException::class)
    abstract fun parse(converter: MySQLConverter): Boolean

    companion object {
        val END_STAMENT_PATTERN = Regex(";\\s*$")
        val INTEGER_PATTERN = Regex("((?:tiny)?(?:small)?int\\s*\\(\\d{1,3}\\))") // (tiny|small)int(12)
        val REAL_PATTERN = Regex("(decimal\\s*\\(\\d*,\\d*\\))") // decimal(5,2)
        val TEXT_PATTERN = Regex("((?:var)?char\\s*\\(\\d*\\))") // (var)char(12)
        val ROW_AUTO_INCREMENT_PATTERN = Regex("(AUTO_INCREMENT)") // (var)char(12)
        val ENGINE_PATTERN = Regex("(?<=\\))(\\s*ENGINE.*)(?=;)")
        val UTF8_PATTERN = Regex("utf8\\w*")
        val CHARACTER_SET_PATTERN = Regex("CHARACTER SET utf8")

        val BAD_COMMA = ReplacePattern(Regex("(,)$(?=\\s*\\))"), "")

        fun match(text: String, pattern: Regex): Boolean {
            return pattern.containsMatchIn(text)
        }

        fun match(text: String, patterns: Array<Regex>): Boolean {
            return patterns.any { it.containsMatchIn(text) }
        }

        fun replacePatterns(text: String, patterns: Array<ReplacePattern>): String {
            var s = text
            for (rp in patterns) {
                s = rp.replace(s)
            }

            return s
        }
    }
}