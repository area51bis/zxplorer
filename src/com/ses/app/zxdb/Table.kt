package com.ses.app.zxdb

import com.ses.app.sql.SQL
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class Table<T>(cls: KClass<*>) {
    private val _rows: HashMap<Any, T> = HashMap()
    val rows: Iterable<T> get() = _rows.values

    private val keyProperty: KProperty<*>? = SQL.getKeyProperty(cls)

    fun addRow(row: T) {
        val key: Any = keyProperty?.getter?.call(row)!!
        _rows[key] = row
    }

    operator fun get(i: Any): T? = _rows[i]
}