package com.ses.app.zxlauncher

import com.ses.util.all
import com.ses.zxdb.dao.Download
import com.ses.zxdb.extension
import com.ses.zxdb.rawExtension
import org.json.JSONObject
import java.io.File

object Config {
    // programas soportados
    private val programs = LinkedHashMap<String, Program>()
    // extensiones soportadas
    private val extensions = HashMap<String, ArrayList<Program>>()

    init {
        val configFile = File(App.workingDir, "config.json")
        if (configFile.exists()) {
            val json = JSONObject(configFile.readText())

            // programs
            json.optJSONArray("programs")?.all<JSONObject>()?.forEach { p ->
                // extensioens soportadas
                val ext = ArrayList<String>()
                p.getJSONArray("ext")?.all<String>()?.forEach {
                    ext.add(it)
                }
                val prog = Program(p.getString("id"),
                        p.getString("name"),
                        p.getString("path"),
                        p.getString("args"),
                        ext.toTypedArray(),
                        p.optBoolean("unzip"))
                programs[prog.id] = prog

                // añadirle a todas las extensiones
                prog.ext.forEach {
                    val extensionPrograms = extensions.getOrPut(it) { ArrayList() }
                    extensionPrograms.add(prog)
                }
            }

            // programas por defecto
            json.optJSONArray("default_programs")?.all<JSONObject>()?.forEach { def ->
                val programId = def.getString("program")
                programs[programId]?.also { program ->
                    def.optJSONArray("ext")?.all<String>()?.forEach { ext ->
                        val list = extensions.getOrPut(ext) { ArrayList() }
                        // ponerle el primero de la lista
                        list.remove(program)
                        list.add(0, program)
                    }
                }
            }
        }
    }

    val allPrograms: Collection<Program> get() = programs.values

    fun getDefaultProgram(download: Download): Program? {
        val extension = download.extension
        return if (extension != null) {
            getDefaultProgram(extension.rawExtension)
        } else {
            null
        }
    }

    fun getPrograms(download: Download): List<Program> {
        val ext = download.extension?.rawExtension
        return extensions[ext] ?: emptyList()
    }

    fun getDefaultProgram(ext: String): Program? {
        val list = extensions[ext]
        return if ((list != null) && list.isNotEmpty()) list[0]
        else null
    }
}