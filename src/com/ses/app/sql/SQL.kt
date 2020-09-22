package com.ses.app.sql

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
        fun getKeyProperty(cls: KClass<*>): KProperty<*>? = cls.members.find { it.hasAnnotation<Key>() } as KProperty<*>
    }

    fun <T : Any> fetch(cls: KClass<T>, f: (row: T) -> Unit) {
        val table = cls.findAnnotation<Table>()
        if (table != null) {
            val tableName = table.name

            conn.createStatement().use { stmt ->
                val map = getColumnsMap(cls)
                val columns = map.values

                stmt?.executeQuery("SELECT ${map.keys.joinToString()} FROM $tableName")?.use { rs ->
                    @Suppress("UNCHECKED_CAST") val ctor: () -> T = cls.primaryConstructor as () -> T

                    while (rs.next()) {
                        val row: T = ctor()

                        columns.forEach {
                            it.read(row, rs)
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
                val isKey = prop.hasAnnotation<Key>()
                map[name] = ColumnInfo(name, prop, isKey)
            }
        }

        return map
    }
}

val RS_DEFAULT_GETTER: (ResultSet, Int) -> Any? = { rs, i -> rs.getBoolean(i) }
val RS_GETTERS: HashMap<KClass<*>, (ResultSet, Int) -> Any?> = hashMapOf(
        Byte::class to { rs, i -> rs.getByte(i) },
        Int::class to { rs, i -> rs.getInt(i) },
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
        if( rsIndex != 0 ) {
            val value = rsGetter(rs, rsIndex!!)
            property.setter.call(thiz, value)
        }
    }
}
