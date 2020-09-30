package com.ses.util

fun String.parse(values: Map<String, Any>) = StringParser(values).parse(this)
fun String.parse(f: ((key: String) -> Any?)? = null) = StringParser().parse(this, f)

/**
 * Sustituye las variables incluidas en una cadena desde un Map u obtenidas mediante una función.
 *
 * El formato de las variables es: `${<var_name>[=<default_value>]}`.
 *
 * Ej.: `"Hola, ${name=Pepe}"`
 *
 * @param values Mapa dónde se buscarán los valores.
 */
class StringParser(private val values: Map<String, Any>? = null) {
    companion object {
        val TEXT_PLACEHOLDER_PATTERN = "(\\$\\{[^}]*})".toRegex()
    }

    /**
     * Transforma una cadena sustituyendo las variables.
     *
     * @param s Cadena.
     * @param f Función opcional para obtener valores.
     */
    fun parse(s: String?, f: ((key: String) -> Any?)? = null): String? {
        if (s == null) return null

        return TEXT_PLACEHOLDER_PATTERN.replace(s) {
            val v = getVariable(it.value)
            var value = if (f != null) f(v.first) else null // primero intenta la función
            if (value == null) value = values?.get(v.first) // si es null, se busca en el map
            value?.toString() ?: v.second // se devuelve el valor como string
        }
    }

    /**
     * Obtiene una variable (nombre y valor por defecto) desde una cadena tipo:
     * ${name[=value]}
     */
    private fun getVariable(placeholder: String): Pair<String, String> {
        val name: String
        val defaultValue: String

        val eqIndex = placeholder.indexOf("=")
        if (eqIndex != -1) {
            name = placeholder.substring(2, eqIndex)
            defaultValue = placeholder.substring(eqIndex + 1, placeholder.length - 1)

        } else {
            name = placeholder.substring(2, placeholder.length - 1)
            defaultValue = ""
        }

        return Pair(name.trim(), defaultValue.trim())
    }
}