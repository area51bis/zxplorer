package com.ses.app.sql

import java.sql.Connection
import java.sql.ResultSet
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.primaryConstructor

class SQL(private val conn: Connection) {
    fun <T : Any> fetch(cls: KClass<T>, f: (row: T) -> Unit) {
        val table = cls.findAnnotation<Table>()
        if (table != null) {
            val tableName = table.name

            conn.createStatement().use { stmt ->
                val map = getColumnsMap(cls)
                val columns = map.values

                stmt?.executeQuery("SELECT ${map.keys.joinToString()} FROM $tableName")?.use { rs ->
                    fillColumnsMap(map, rs)
                    @Suppress("UNCHECKED_CAST") val ctor: () -> T = cls.primaryConstructor as () -> T

                    columns.forEach { println("${it.name} -> ${it.property.name} (${it.rsIndex})") }

                    while (rs.next()) {
                        val row: T = ctor()

                        columns.forEach {
                            val index = it.rsIndex
                            if (index != null) {
                                val value = rs.getObject(index)
                                it.property.setter.call(row, value)
                            }
                        }

                        f(row)
                    }
                }
            }
        }
    }

    private fun <T : Any> getColumnsMap(cls: KClass<T>): Map<String, ColumnInfo> {
        val map: LinkedHashMap<String, ColumnInfo> = LinkedHashMap()

        cls.members.filter { it.hasAnnotation<Column>() }.forEach { prop ->
            if (prop is KMutableProperty) {
                val c = prop.findAnnotation<Column>()
                val name = if (c?.name != "") c!!.name else prop.name
                map[name] = ColumnInfo(name, prop)
            }
        }

        return map
    }

    private fun fillColumnsMap(map: Map<String, ColumnInfo>, rs: ResultSet) {
        for (i in 1..rs.metaData.columnCount) {
            val columnName = rs.metaData.getColumnName(i)
            map[columnName]?.apply {
                rsIndex = i
            }
        }
    }
}

data class ColumnInfo(val name: String, val property: KMutableProperty<*>) {
    var rsIndex: Int? = null
}
