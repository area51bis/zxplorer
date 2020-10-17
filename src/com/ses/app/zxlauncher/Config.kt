package com.ses.app.zxlauncher

import com.ses.app.zxlauncher.model.EntryDownload
import com.ses.util.all
import com.ses.zxdb.dao.Download
import com.ses.zxdb.extension
import com.ses.zxdb.rawExtension
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

object Config {
    // programas soportados
    private val programs = LinkedHashMap<String, Program>()

    // extensiones soportadas
    private val extensions = HashMap<String, ArrayList<Program>>()

    init {
        val progsDir = File(App.workingDir, "progs")
        if (progsDir.exists()) {
            val progList = ArrayList<Program>()

            // cargar programas
            progsDir.listFiles { file -> file.extension == "json" }?.forEach { file ->
                if (file.nameWithoutExtension != "defaults") {
                    progList.add(loadProgram(file))
                }
            }

            // ordenar lista
            progList.sortedBy { it.order }.forEach { prog ->
                // añadir
                programs[prog.id] = prog

                // añadirle a todas las extensiones
                prog.ext.forEach {
                    val extensionPrograms = extensions.getOrPut(it) { ArrayList() }
                    extensionPrograms.add(prog)
                }
            }

            // programas por defecto
            File(progsDir, "defaults.json").also {
                if (it.exists()) {
                    loadDefaults(it)
                }
            }

        }
    }

    private fun loadDefaults(file: File) {
        val json = JSONArray(file.readText())

        json.all<JSONObject>().forEach { def ->
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

    private fun loadProgram(file: File): Program {
        val json = JSONObject(file.readText())

        // extensioens soportadas
        val ext = ArrayList<String>()
        json.getJSONArray("ext")?.all<String>()?.forEach {
            ext.add(it)
        }
        val prog = Program(json.getString("id"),
                json.getString("name"),
                json.getString("path"),
                json.getString("args"),
                ext.toTypedArray(),
                json.optBoolean("unzip"))
        prog.order = json.optInt("order", 1)
        //programs[prog.id] = prog

        return prog
    }

    val allPrograms: Collection<Program> get() = programs.values

    fun getDefaultProgram(download: EntryDownload): Program? {
        val extension = download.getExtension()
        return if (extension != null) {
            getDefaultProgram(extension.rawExtension)
        } else {
            null
        }
    }

    fun getPrograms(download: EntryDownload): List<Program> {
        val ext = download.getExtension()?.rawExtension
        return extensions[ext] ?: emptyList()
    }

    fun getDefaultProgram(ext: String): Program? {
        val list = extensions[ext]
        return if ((list != null) && list.isNotEmpty()) list[0]
        else null
    }
}