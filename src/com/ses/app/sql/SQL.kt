package com.ses.app.sql

import java.sql.Connection
import java.sql.ResultSet
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.primaryConstructor

class SQL(private val conn: Connection) {
    fun <T: Any> fetch(cls: KClass<T>, f: (row: T) -> Unit) {
        val table = cls.findAnnotation<Table>()
        if (table != null) {
            val tableName = table.name

            conn.createStatement().use { stmt ->
                stmt?.executeQuery("SELECT * from $tableName")?.use { rs ->
                    cls.primaryConstructor?.call()
                    val colInf = getColumnsInfo(cls, rs)

                    //while (rs.next()) {}
                    val i = 3
                }
            }
        }
    }

    private fun <T: Any> getColumnsInfo(cls: KClass<T>, rs: ResultSet): ArrayList<ColumnInfo> {
        val colInf: ArrayList<ColumnInfo> = ArrayList()

        // columnas en cls
        val propMap: HashMap<String, KCallable<*>> = HashMap()
        cls.members.filter { it.hasAnnotation<Column>() }.forEach { prop ->
            propMap[prop.name] = prop
        }

        // recorrer columnas en rs
        for( i in 1..rs.metaData.columnCount ) {
            val columnName = rs.metaData.getColumnName(i)
            propMap[columnName]?.let { prop ->
                colInf.add(ColumnInfo(prop, columnName, i))
            }
        }

        return colInf
    }
}

data class ColumnInfo(val property: KCallable<*>, val name: String, val index: Int)