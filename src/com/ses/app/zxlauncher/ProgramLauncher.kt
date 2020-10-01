package com.ses.app.zxlauncher

import com.ses.util.parse
import java.io.File

class ProgramLauncher(val id: String, val name: String, val path: String, val args: String = "\${filePath}", val unzip: Boolean = false) {
    private val cmd: ArrayList<String> = ArrayList()
    private val dir = File(path).parentFile

    init {
        cmd.add(path)
        cmd.addAll(args.split("\\s+".toRegex()).toTypedArray())
    }

    fun launch(file: File) {
        val map = mapOf<String, Any>(
                "filePath" to file.absolutePath
        )

        ProcessBuilder(cmd.map { it.parse(map) })
                .directory(dir)
                .start()
    }
}
