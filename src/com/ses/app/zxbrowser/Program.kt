package com.ses.app.zxbrowser

import com.ses.util.SysUtil
import com.ses.util.parse
import java.io.File
import java.util.zip.ZipFile

/**
 * Definición de programa (no tiene porqué ser un emulador).
 */
class Program(var id: String, var name: String, var path: String, var args: String = "\${filePath}", var ext: Array<String> = emptyArray(), var unzip: Boolean = false) : Cloneable {
    var defaultFor: Array<String> = emptyArray()
    private val cmd: List<String>?
    private val dir = File(path).parentFile

    init {
        cmd = when {
            SysUtil.isWindows -> listOf("cmd", "/C", "$path $args")
            SysUtil.isLinux -> listOf("/bin/bash", "-c", "$path ${escapeLinuxCommand(args)}")
            else -> null
        }
    }

    fun launch(file: File) {
        if (unzip) {
            var unzippedFile: File?

            //TODO: ¿sacar a una función / extensión?
            ZipFile(file).use { zip ->
                val entry = zip.entries().toList().first { !it.isDirectory }
                zip.getInputStream(entry).use { input ->
                    val tempDir = File(App.workingDir, "temp")
                    unzippedFile = File(tempDir, entry.name).also { f ->
                        f.parentFile.mkdirs()
                        //println("Unizipping ${f.absolutePath}")
                        f.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                }
            }

            if (unzippedFile != null) doLaunch(unzippedFile!!)

        } else {
            doLaunch(file)
        }
    }

    private fun doLaunch(file: File) {
        val map = mapOf<String, Any>(
                "filePath" to quoteArg(file.absolutePath)
        )

        println(cmd?.map { it.parse(map) }?.joinToString(separator = " "))
        ProcessBuilder(cmd?.map { it.parse(map) })
                .directory(dir)
                .start()
    }

    private fun quoteArg(arg: String): String = if (arg.contains(' ')) "\"$arg\"" else arg

    private fun escapeLinuxCommand(s: String): String = s.replace("\\", "\\\\") // '\' -> '\\'
            .replace("\"", "\\\\\\\"") // '"' -> '\\\"'

    public override fun clone(): Program = Program(id, name, path, args, ext.clone(), unzip).also {
        it.defaultFor = defaultFor.clone()
    }

    override fun toString(): String {
        return name
    }
}
