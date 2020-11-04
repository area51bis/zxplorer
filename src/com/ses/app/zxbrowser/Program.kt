package com.ses.app.zxbrowser

import com.ses.util.parse
import java.io.File
import java.util.zip.ZipFile

/**
 * Definición de programa (no tiene porqué ser un emulador).
 */
class Program(var id: String, var name: String, var path: String, var args: String = "\${filePath}", var ext: Array<String> = emptyArray(), var unzip: Boolean = false) : Cloneable {
    private val cmd: ArrayList<String> = ArrayList()
    private val dir = File(path).parentFile

    init {
        cmd.add(path)
        cmd.addAll(args.split("\\s+".toRegex()).toTypedArray())
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
                "filePath" to file.absolutePath
        )

        ProcessBuilder(cmd.map { it.parse(map) })
                .directory(dir)
                .start()
    }

    public override fun clone(): Program = Program(id, name, path, args, ext, unzip)

    override fun toString(): String {
        return name
    }
}
