package com.ses.sql

import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure

class SQL(private val conn: Connection) {
    companion object {
        fun getTableName(cls: KClass<*>): String? = cls.findAnnotation<Table>()?.name

        /** primero busca [@Query], luego [@Table]. */
        fun getQuery(cls: KClass<*>): String? = cls.findAnnotation<Query>()?.query
                ?: "SELECT * FROM ${getTableName(cls)}"

        fun getKeyProperty(cls: KClass<*>): KProperty<*>? = cls.members.find { it.hasAnnotation<Key>() } as KProperty<*>
    }

    fun select(query: String, f: (row: ResultSet) -> Unit) {
        conn.createStatement().use { stmt ->
            stmt?.executeQuery(query)?.use { rs -> f(rs) }
        }
    }

    fun <T : Any> select(cls: KClass<T>, f: (row: T) -> Unit) {
        select(query = null, cls = cls, f = f)
    }

    fun <T : Any> select(query: String? = null, cls: KClass<T>, f: (row: T) -> Unit) {
        select(query ?: getQuery(cls)!!) { rs ->
            val columsInfo = getColumnsMap(cls).values
            @Suppress("UNCHECKED_CAST") val ctor: () -> T = cls.primaryConstructor as () -> T

            while (rs.next()) {
                val row: T = ctor()

                columsInfo.forEach {
                    it.read(row, rs)
                }

                f(row)
            }
        }
    }

    fun <T : Any> select(columns: Array<String> = arrayOf("*"), from: String? = null, where: String? = null, orderBy: String? = null, cls: KClass<T>, f: (row: T) -> Unit) {
        val table = from ?: getTableName(cls)

        if (table != null) {
            val query = StringBuilder("SELECT ${columns.joinToString()} FROM $table").apply {
                if (where != null) append(" WHERE $where")
                if (orderBy != null) append(" ORDER BY $orderBy")
            }.toString()

            select(query, cls, f)
        }
    }

    private fun <T : Any> getColumnsMap(cls: KClass<T>): Map<String, ColumnInfo> {
        val map: LinkedHashMap<String, ColumnInfo> = LinkedHashMap()

        cls.members.filter { it.hasAnnotation<Column>() }.forEach { prop ->
            if (prop is KMutableProperty) {
                val c = prop.findAnnotation<Column>()
                val name = if (c?.name != "") c!!.name else prop.name
                val isKey = prop.hasAnnotation<Key>()
                map[name] = ColumnInfo(name, prop, isKey)
            }
        }

        return map
    }
}

val RS_DEFAULT_GETTER: (ResultSet, Int) -> Any? = { rs, i -> rs.getObject(i) }
val RS_GETTERS: HashMap<KClass<*>, (ResultSet, Int) -> Any?> = hashMapOf(
        Byte::class to { rs, i -> rs.getByte(i) },
        Int::class to { rs, i -> if (rs.getObject(i) != null) rs.getInt(i) else null },
        Long::class to { rs, i -> rs.getLong(i) },
        Float::class to { rs, i -> rs.getFloat(i) },
        Double::class to { rs, i -> rs.getDouble(i) },
        Boolean::class to { rs, i -> rs.getBoolean(i) },
        String::class to { rs, i -> rs.getString(i) }
)

class ColumnInfo(val name: String, val property: KMutableProperty<*>, val isKey: Boolean = false) {
    private var rsIndex: Int? = null

    private val rsGetter: ((ResultSet, Int) -> Any?) by lazy {
        RS_GETTERS[property.returnType.jvmErasure] ?: RS_DEFAULT_GETTER
    }

    fun read(thiz: Any, rs: ResultSet) {
        if (rsIndex == null) {
            rsIndex = try {
                rs.findColumn(name)
            } catch (e: SQLException) {
                0
            }
        }

        // rsIndex no debería ser null
        if (rsIndex != 0) {
            val value = rsGetter(rs, rsIndex!!)
            property.setter.call(thiz, value)
        }
    }
}
