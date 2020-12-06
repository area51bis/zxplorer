package com.ses.app.zxbrowser

import com.ses.util.SysUtil
import com.ses.util.parse
import java.io.File
import java.util.zip.ZipFile

/**
 * Definición de programa (no tiene porqué ser un emulador).
 */
class Program(var name: String, var path: String, var args: String = "\${filePath}", var ext: Array<String> = emptyArray(), var unzip: Boolean = false) : Cloneable {
    var defaultFor: Array<String> = emptyArray()
    private val cmd: List<String>?
    private val dir = File(path).parentFile

    init {
        cmd = when {
            SysUtil.isWindows -> listOf("cmd", "/S", "/C", "\"${escapePath(path)} $args\"")
            SysUtil.isLinux -> listOf("/bin/bash", "-c", "${escapeUnixPath(path)} ${escapeLinuxArgs(args)}")
            SysUtil.isMac -> listOf("open", path, "--args", *args.split(" ").map { escapeOSXArgs(it) }.toTypedArray())
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
                    val tempDir = App.localFile("temp")
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
        // parece que mac no necesita nada
        val map = mapOf<String, Any>(
                "filePath" to escapePath(file.absolutePath)
        )

        //println(cmd?.map { it.parse(map) }?.joinToString(separator = " "))
        ProcessBuilder(cmd?.map { it.parse(map) })
                .directory(dir)
                .start()
    }

    private fun escapePath(path: String): String = if (path.contains(' ') && (SysUtil.isWindows || SysUtil.isLinux)) {
        "\"$path\""
    } else {
        path
    }

    private fun escapeLinuxArgs(s: String): String = s.replace("\\", "\\\\") // '\' -> '\\'
            .replace("\"", "\\\\\\\"") // '"' -> '\\\"'

    private fun escapeUnixPath(s: String): String = s.replace(" ", "\\ ") // '' -> '\ '

    private fun escapeOSXArgs(s: String): String = s.replace("\\", "\\\\") // '\' -> '\\'
            .replace("\"", "\\\"") // '"' -> '\"'

    public override fun clone(): Program = Program(name, path, args, ext.clone(), unzip).also {
        it.defaultFor = defaultFor.clone()
    }

    override fun toString(): String {
        return name
    }
}
