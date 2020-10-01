package com.ses.app.zxlauncher

import org.json.JSONObject
import java.io.File

object Config {
    val programs = LinkedHashMap<String, ProgramLauncher>()

    init {
        val configFile = File(App.workingDir, "config.json")
        if (configFile.exists()) {
            val json = JSONObject(configFile.readText())

            // programs
            json.optJSONArray("programs")?.also { arr ->
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    val prog = ProgramLauncher(o.getString("id"),
                            o.getString("name"),
                            o.getString("path"),
                            o.getString("args"),
                            o.optBoolean("unzip"))
                    programs[prog.id] = prog
                }
            }
        }
    }

    fun getDefaultProgram(ext: String): ProgramLauncher? {
        return programs.values.first()
    }
}