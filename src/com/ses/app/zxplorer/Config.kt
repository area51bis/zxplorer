package com.ses.app.zxplorer

import com.google.gson.*
import com.google.gson.stream.JsonWriter
import com.ses.app.zxplorer.model.ModelDownload
import com.ses.util.*
import java.io.Writer

object Config {
    private val configFile = App.localFile("config.json")
    private val gson = GsonBuilder()
            .disableHtmlEscaping()
            .create()
    private val config: JsonObject

    // programas soportados
    private val programs = ArrayList<Program>()

    // extensiones soportadas
    private val extensions = HashMap<String, ArrayList<Program>>()

    // bibliotecas
    private val libraries = ArrayList<Library>()

    init {
        config = if (configFile.exists()) {
            //JSONObject(configFile.readText())
            JsonParser.parseReader(configFile.reader()).asJsonObject
        } else {
            val stream = javaClass.getResourceAsStream("/default_config.json")
            val text = stream.bufferedReader().use { it.readText() }
            configFile.writeText(text)
            //JSONObject(text)
            JsonParser.parseString(text).asJsonObject
        }

        loadPrograms(config.optJsonArray("programs"))
        loadLibraries(config.optJsonArray("libraries"))
    }

    private fun jsonWriter(writer: Writer) = JsonWriter(writer).apply { setIndent("    ") }

    private fun save() {
        configFile.writer().use {
            gson.toJson(config, jsonWriter(it))
        }
    }

    // cambia la lista de programas
    fun setPrograms(list: List<Program>) {
        // reescribe la lista de programas
        val arr = config.getOrCreateJSONArray("programs")
        arr.clear()
        list.forEach { program ->
            val o = JsonObject()
            o.addProperty("name", program.name)
            o.addProperty("path", program.path)
            o.addProperty("args", program.args)
            o.add("ext", JsonArray(program.ext))
            if (program.unzip) o.addProperty("unzip", true)
            if (program.defaultFor.isNotEmpty()) o.add("default_for", JsonArray(program.defaultFor))
            arr.add(o)
        }

        // guarda en disco
        save()

        // recarga
        programs.clear()
        extensions.clear()
        loadPrograms(config.optJsonArray("programs"))
    }

    private fun loadPrograms(json: JsonArray?) {
        json?.forEach { def ->
            val prog = loadProgram(def as JsonObject)
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

    private fun loadProgram(json: JsonObject): Program {
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
            val o = JsonObject()
            o.addProperty("type", lib.type)
            o.addProperty("name", lib.name)
            o.addProperty("path", lib.path)
            if (lib.source != null) o.addProperty("source", lib.source)
            arr.add(o)
        }

        // guarda en disco
        save()

        // recarga
        libraries.clear()
        libraries.addAll(list)
    }

    private fun loadLibraries(json: JsonArray?) {
        json?.forEach { o ->
            if (o is JsonObject) {
                val lib = Library(o.getString("type"), o.getString("name"), o.getString("path"), o.optString("source", null))
                libraries.add(lib)
            }
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