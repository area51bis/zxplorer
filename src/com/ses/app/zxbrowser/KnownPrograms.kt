package com.ses.app.zxbrowser

import java.io.File

object KnownPrograms {
    //TODO meter en JSON
    private val map = hashMapOf(
            "zxspin" to Program("zxspin", "ZXSpin", "", "\${filePath}", arrayOf("tzx", "tap", "z80", "sna")),
            "zesarux" to Program("zesarux", "ZEsarUX", "", "--noconfigfile --realloadfast --realtape \${filePath}", arrayOf("tzx", "tap", "z80", "sna"), true),
            "fuse" to Program("fuse", "Fuse", "", "--auto-load --tape \${filePath}", arrayOf("tzx", "tap", "z80", "sna"), true),
            "retrovirtualmachine" to Program("rvm", "Retro Virtual Machine", "", "-b=zx48k -w -p -c=j\"\"\\n -i \${filePath}", arrayOf("tzx", "tap"), true),
    )

    private val genericProgram = Program("?", "?", "", "\${filePath}", arrayOf("tzx", "tap", "z80", "sna"))

    fun get(file: File): Program {
        var prog: Program? = map[file.nameWithoutExtension.toLowerCase()]
        if (prog == null) {
            prog = genericProgram
            prog.id = file.nameWithoutExtension.toLowerCase()
            prog.name = file.nameWithoutExtension.capitalize()
        }
        prog.path = file.absolutePath

        return prog
    }
}