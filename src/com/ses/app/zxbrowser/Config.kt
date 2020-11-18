package com.ses.app.zxbrowser

import com.ses.app.zxbrowser.model.ModelDownload
import com.ses.util.all
import com.ses.util.clear
import com.ses.util.getArray
import com.ses.util.getOrCreateJSONArray
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

object Config {
    private val configFile = File(App.workingDir, "config.json")
    private val config: JSONObject

    // programas soportados
    private val programs = ArrayList<Program>()

    // extensiones soportadas
    private val extensions = HashMap<String, ArrayList<Program>>()

    // bibliotecas
    private val libraries = ArrayList<Library>()

    init {
        config = if (configFile.exists()) {
            JSONObject(configFile.readText())
        } else {
            val stream = javaClass.getResourceAsStream("/default_config.json")
            val text = stream.bufferedReader().use { it.readText() }
            configFile.writeText(text)
            JSONObject(text)
        }

        loadPrograms(config.optJSONArray("programs"))
        loadLibraries(config.optJSONArray("libraries"))
    }

    private fun save() {
        configFile.writer().use {
            config.write(it, 4, 0)
        }
    }

    // cambia la lista de programas
    fun setPrograms(list: List<Program>) {
        // reescribe la lista de programas
        val arr = config.getOrCreateJSONArray("programs")
        arr.clear()
        list.forEach { program ->
            val o = JSONObject()
            o.put("name", program.name)
            o.put("path", program.path)
            o.put("args", program.args)
            o.put("ext", JSONArray(program.ext))
            if (program.unzip) o.put("unzip", true)
            if (program.defaultFor.isNotEmpty()) o.put("default_for", JSONArray(program.defaultFor))
            arr.put(o)
        }

        // guarda en disco
        save()

        // recarga
        programs.clear()
        extensions.clear()
        loadPrograms(config.optJSONArray("programs"))
        //loadDefaults(config.optJSONArray("default_programs"))
    }

    private fun loadPrograms(json: JSONArray?) {
        json?.all<JSONObject>()?.forEach { def ->
            val prog = loadProgram(def)
            programs.add(prog)

            prog.ext.forEach { ext ->
                val extensionPrograms = extensions.getOrPut(ext) { ArrayList() }
                extensionPrograms.add(prog)
            }
        }

        programs.forEach { prog ->
            prog.defaultFor.forEach { ext ->
                val list = extensions.getOrPut(ext) { ArrayList() }
                // ponerle el primero de la lista
                list.remove(prog)
                list.add(0, prog)
            }
        }
    }

    private fun loadProgram(json: JSONObject): Program {
        return Program(json.getString("name"),
                json.getString("path"),
                json.getString("args"),
                //ext.toTypedArray(),
                json.getArray("ext"),
                json.optBoolean("unzip")).apply {
            defaultFor = json.getArray("default_for")
        }
    }

    // cambia la lista de programas
    fun setLibraries(list: List<Library>) {
        // reescribe la lista de programas
        val arr = config.getOrCreateJSONArray("libraries")
        arr.clear()
        list.forEach { lib ->
            val o = JSONObject()
            o.put("type", lib.type)
            o.put("name", lib.name)
            o.put("path", lib.path)
            arr.put(o)
        }

        // guarda en disco
        save()

        // recarga
        libraries.clear()
        libraries.addAll(list)
    }

    private fun loadLibraries(json: JSONArray?) {
        json?.all<JSONObject>()?.forEach { o ->
            val lib = Library(o.getString("type"), o.getString("name"), o.getString("path"))
            libraries.add(lib)
        }
    }

    val allLibraries: Collection<Library> get() = libraries

    val allPrograms: Collection<Program> get() = programs

    fun getDefaultProgram(download: ModelDownload): Program? {
        val extension = download.getExtension()
        return if (extension != null) {
            getDefaultProgram(download.getRawExtension())
        } else {
            null
        }
    }

    fun getPrograms(download: ModelDownload): List<Program> {
        val ext = download.getRawExtension().toLowerCase()
        return extensions[ext] ?: emptyList()
    }

    fun getDefaultProgram(ext: String): Program? {
        val list = extensions[ext.toLowerCase()]
        return if ((list != null) && list.isNotEmpty()) list[0]
        else null
    }

    object general {
        val showRootNode: Boolean = false
    }
}