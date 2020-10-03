package com.ses.app.zxlauncher

import com.ses.util.all
import com.ses.zxdb.dao.Download
import com.ses.zxdb.extension
import com.ses.zxdb.rawExtension
import org.json.JSONObject
import java.io.File

object Config {
    private val programs = LinkedHashMap<String, ProgramLauncher>()
    private val defaultPrograms = HashMap<String, ProgramLauncher>()

    init {
        val configFile = File(App.workingDir, "config.json")
        if (configFile.exists()) {
            val json = JSONObject(configFile.readText())

            // programs
            json.optJSONArray("programs")?.all<JSONObject>()?.forEach { p ->
                val ext = ArrayList<String>()
                p.getJSONArray("ext")?.all<String>()?.forEach {
                    ext.add(it)
                }
                val prog = ProgramLauncher(p.getString("id"),
                        p.getString("name"),
                        p.getString("path"),
                        p.getString("args"),
                        ext.toTypedArray(),
                        p.optBoolean("unzip"))
                programs[prog.id] = prog
            }

            json.optJSONArray("default_programs")?.all<JSONObject>()?.forEach { def ->
                val programId = def.getString("program")
                programs[programId]?.also { program ->
                    def.optJSONArray("ext")?.all<String>()?.forEach { ext ->
                        defaultPrograms[ext] = program
                    }
                }
            }
        }
    }

    val allPrograms: Collection<ProgramLauncher> get() = programs.values

    fun getDefaultProgram(download: Download): ProgramLauncher? {
        val extension = download.extension
        return if (extension != null) {
            getDefaultProgram(extension.rawExtension)
        } else {
            null
        }
    }

    fun getDefaultProgram(ext: String): ProgramLauncher? {
        return defaultPrograms[ext]
    }
}