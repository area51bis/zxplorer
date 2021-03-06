package com.ses.zxdb

import com.ses.sql.SQL
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class Table<T>(cls: KClass<*>) {
    private val _rows: LinkedHashMap<Any, T> = LinkedHashMap()
    val rows: Iterable<T> get() = _rows.values

    private val keyProperty: KProperty<*>? = SQL.getKeyProperty(cls)

    fun addRow(row: T) {
        val key: Any = keyProperty?.getter?.call(row)!!
        _rows[key] = row
    }

    operator fun get(key: Any?): T? = if (key != null) _rows[key] else null
}