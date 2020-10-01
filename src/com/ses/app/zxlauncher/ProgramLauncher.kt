package com.ses.app.zxlauncher

import com.ses.util.parse
import java.io.File

/*
/Applications/zesarux.app/Contents/MacOS/zesarux --realtape '/Users/mmoreno/proyectos/github/zxlauncher/run/pub/sinclair/games/a/abu.tzx'
*/
class ProgramLauncher(val id: String, val name: String, val path: String, val args: String? = null, val unzip: Boolean = false) {
    fun launch(file: File) {
        val map = mapOf<String, Any>(
                "filePath" to file.absolutePath
        )

        val cmd = if (args != null) "$path ${args.parse(map)}" else path
        //ProcessBuilder(*cmd.split(" ").toTypedArray())
        ProcessBuilder("bash", "-c", cmd)
                .directory(App.workingDir)
                .start()
    }
}
