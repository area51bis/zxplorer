package com.ses.app.zxlauncher

import com.ses.util.parse
import java.io.File
import java.util.zip.ZipFile

class Program(val id: String, val name: String, val path: String, val args: String = "\${filePath}", val ext: Array<String> = emptyArray(), val unzip: Boolean = false) {
    var order = 0

    private val cmd: ArrayList<String> = ArrayList()
    private val dir = File(path).parentFile

    init {
        cmd.add(path)
        cmd.addAll(args.split("\\s+".toRegex()).toTypedArray())
    }

    fun launch(file: File) {
        if (unzip) {
            var unzippedFile: File?

            //TODO: sacar a una función / extensión
            ZipFile(file).use { zip ->
                val entry = zip.entries().nextElement()
                zip.getInputStream(entry).use { input ->
                    val tempDir = File(App.workingDir, "temp")
                    unzippedFile = File(tempDir, entry.name).also { f->
                        f.parentFile.mkdirs()
                        println("Unizipping ${f.absolutePath}")
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

    override fun toString(): String {
        return name
    }
}
